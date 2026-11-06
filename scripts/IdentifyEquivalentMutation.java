import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.gregor.GregorMutationEngine;
import org.pitest.mutationtest.engine.gregor.config.DefaultMutationEngineConfiguration;
import org.pitest.mutationtest.engine.gregor.config.Mutator;

/** Recovers the PIT identifier from bytecode without running a mutation test campaign. */
class IdentifyEquivalentMutation {
    public static void main(String[] args) throws Exception {
        Path classes = Path.of(args.length == 0 ? "target/classes" : args[0]);
        String className = "br.ifsp.demo.domain.TipoIngresso";
        byte[] bytes = Files.readAllBytes(classes.resolve(className.replace('.', '/') + ".class"));
        var configuration = new DefaultMutationEngineConfiguration(
                method -> method.getName().equals("hashCode"), Mutator.byName("PRIMITIVE_RETURNS"));
        var engine = new GregorMutationEngine(configuration);
        var mutater = engine.createMutator(name -> name.replace('/', '.').equals(className)
                ? Optional.of(bytes) : Optional.empty());
        var mutations = mutater.findMutations(ClassName.fromString(className));
        if (mutations.size() != 1) {
            throw new IllegalStateException("Expected one hashCode return mutation, got " + mutations.size());
        }
        var mutation = mutations.getFirst();
        System.out.println("PIT: 1.18.1 (use the matching pitest JAR)");
        System.out.println("Class SHA-256: " + HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(bytes)));
        System.out.println("Line: " + mutation.getLineNumber());
        System.out.println("ID: " + mutation.getId());
        System.out.println("Description: " + mutation.getDescription());
        System.out.println("No tests executed; historical mutation status is unchanged.");
    }
}
