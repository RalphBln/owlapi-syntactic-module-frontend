package de.ontomed.simpleanno.modularization;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

/**
 * @author ralph
 */
public class SyntacticLocalityModularizationFrontend {

    private OWLOntology onto;

    /**
     *
     * @param args
     *   args[0]: (IRI) original ontology location
     *   args[1]: (file path) path to the signature file
     *   args[2]: ("top" | "bottom" | "nested") module type
     *   args[3]: (file path) path to the destination file
     *   args[4]: (IRI) IRI of the extracted ontology module
     */
    public SyntacticLocalityModularizationFrontend(String[] args) {
        try {

            IRI loadIRI = (IRI.create(args[0]));
            System.out.printf("Loading ontology from %s...%n", loadIRI.getIRIString());

            OWLOntologyManager om = OWLManager.createOWLOntologyManager();
            onto = om.loadOntology(loadIRI);

            int originalSize = onto.getAxiomCount();

            System.out.printf("Loaded ontology with IRI %s. Size: %d%n", onto.getOntologyID().getOntologyIRI().get().getIRIString(), originalSize);

            System.out.printf("Reading signature file from %s...%n", args[1]);
            Set<OWLEntity> signature = null;
            try {
                signature = readSignatureFile(new File(args[1]));
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.printf("Extracting module of type %s...%n", args[2]);

            SyntacticLocalityModuleExtractor ex = new SyntacticLocalityModuleExtractor(om, onto, ModuleType.valueOf(args[2]));
            Set<OWLAxiom> module = ex.extract(signature);

            System.out.printf("Creating new ontology from extracted module...%n");

            OWLDocumentFormat originalFormat = om.getOntologyFormat(onto);
            if (originalFormat == null) {
                // should not happen, but who knows
                originalFormat = new FunctionalSyntaxDocumentFormat();
            }

            om = OWLManager.createOWLOntologyManager();
            onto = om.createOntology(args.length == 5 ? IRI.create(args[4]) : onto.getOntologyID().getOntologyIRI().get());

            onto.addAxioms(module);

            System.out.printf("Saving extracted module to %s...%n", args[3]);

            om.saveOntology(onto, originalFormat, new FileOutputStream(args[3]));

            System.out.printf("Module written. Reduced size from %d to %d axioms.%n", originalSize, module.size());

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SyntacticLocalityModularizationFrontend(args);
    }

    private Set<OWLEntity> readSignatureFile(File file) throws FileNotFoundException, IOException {
        HashSet<OWLEntity> signature = new HashSet<>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#"))
                continue;

            IRI iri = IRI.create(line);
            signature.addAll(onto.getEntitiesInSignature(iri));
        }
        in.close();
        return signature;
    }

}
