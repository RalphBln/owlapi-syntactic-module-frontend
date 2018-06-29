# owlapi-syntactic-module-frontend
A simple command-line frontend for the syntactic locality based module extraction algorithm that is built into the OWL API

**Usage:**

`java -cp owlapi-syntactic-module-frontend-1.0-SNAPSHOT-jar-with-dependencies.jar de.ontomed.simpleanno.modularization.SyntacticLocalityModularizationFrontend <ontology location> <signature file> <module type> <destination file> <new IRI>`

1. `ontology location`: (IRI) original ontology location
1. `signature file`: (file path) path to the signature file
1. `module type`: ("TOP" | "BOTTOM" | "STAR") The module type
1. `destination file`: (file path) path to the destination file
1. `new IRI`: (IRI) *optional* IRI of the extracted ontology module
