**MAJAN**

MAJAN is an extension of **AJAN** agent engineering tool to support
SPARQL-BT-based **multiagent** **coordination** **into** **groups**.

MAJAN consists of **MAJAN Plugin** and **MAJAN** **Web** for [AJAN
Service](https://github.com/aantakli/AJAN-service) and [AJAN
Editor](https://github.com/aantakli/AJAN-editor), respectively. These
extensions are integrated to AJAN and can be found in AJAN\_w\_MAJAN and
AJAN\_Editor\_w\_MAJAN folders.

Moreover, MAJAN provides **postman collections** in “Postman
Collections” folder to create and execute MAC use-cases easily.

Additionally, **MAJAN Web** supports **executing centrally running
grouping algorithms** such as
[HDBSCAN](https://dl.acm.org/doi/10.1145/2733381) to solve Clustering
and [BOSS](https://ojs.aaai.org/index.php/AAAI/article/view/17879) to
solve CSGP. Necessary configuration files for MAJAN Web and source code
of algorithms are provided in Grouping Algorithms folder.

Furthermore, MAJAN provides template SPARQL-BTs in SPARQL-BTs-for-MAC to
execute FIPA Request, CSGP and Clustering coordination protocols.

1)  **Request Protocol** enables agents to exchange some information
    through coordination.

2)  **CSGP Protocol** enables agents to coordinate themselves into
    coalitions (i.e. groups) by using BOSS, which is a CSGP solver
    algorithm. BOSS can be replaced with other CSGP solver algorithms
    easily in the respective BT.

3)  **Clustering Protocol** enables agents to coordinate themselves into
    clusters (i.e. groups) by using HDBSCAN which is a Clustering solver
    algorithm. HDBSCAN can be replaced with other Clustering solver
    algorithms easily in the respective BT.

Finally, MAJAN is provided with a guideline document (MAJAN
Guide-v1.2.pdf) which includes instructions to use MAJAN. As well as, it
covers the ontology that is used in MAJAN.

Finally, MAJAN is provided with a guideline document which includes
everything about MAJAN, starting from an overview, MAC ontology, agent
communication, execution of MAC use-cases to evaluation of results.
Moreover, it provides some extra tips for users which might be helpful
while designing and executing a MAC use-case. Please refer to “MAJAN
Guide.pdf” for more detailed information.
