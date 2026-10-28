package tn.drepanocytose;

import java.io.File;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String xmlPath = "src/main/resources/patients.xml";
        String xsdPath = "src/main/resources/patients.xsd";

        // 1️⃣ Validation XML/XSD
        System.out.println("=== Validation XML/XSD ===");
        try {
            File xmlFile = new File(xmlPath);
            File xsdFile = new File(xsdPath);

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsdFile);

            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlFile));

            System.out.println("Validation réussie : le XML est conforme au XSD !");
        } catch (SAXException e) {
            System.out.println("Erreur de validation : " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("Erreur d'accès au fichier : " + e.getMessage());
            return;
        }

        // 2️⃣ Interrogation XPath et affichage complet
        System.out.println("\n=== Affichage complet du patient ===");
        try {
            File xmlFile = new File(xmlPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Informations principales
            String nom = xpathGet(doc, "/Patient/Nom");
            String prenom = xpathGet(doc, "/Patient/Prenom");
            String dateNaissance = xpathGet(doc, "/Patient/DateNaissance");
            String sexe = xpathGet(doc, "/Patient/Sexe");
            String groupe = xpathGet(doc, "/Patient/GroupeSanguin");
            String typeDrepan = xpathGet(doc, "/Patient/TypeDrepanocytose");

            System.out.println("Patient : " + nom + " " + prenom);
            System.out.println("Date de naissance : " + dateNaissance);
            System.out.println("Sexe : " + sexe);
            System.out.println("Groupe sanguin : " + groupe);
            System.out.println("Type de drépanocytose : " + typeDrepan);

            // Traitement
            System.out.println("\nTraitement :");
            System.out.println("Nom : " + xpathGet(doc, "/Patient/Traitement/Nom"));
            System.out.println("Début : " + xpathGet(doc, "/Patient/Traitement/Debut"));
            System.out.println("Posologie : " + xpathGet(doc, "/Patient/Traitement/Posologie"));
            System.out.println("Observation : " + xpathGet(doc, "/Patient/Traitement/Observation"));

            // Crises
            System.out.println("\nCrises :");
            NodeList crises = xpathGetNodes(doc, "//Crise");
            for (int i = 0; i < crises.getLength(); i++) {
                Element crise = (Element) crises.item(i);
                System.out.println("ID : " + crise.getAttribute("id"));
                System.out.println("Date : " + xpathGet(crise, "Date"));
                System.out.println("Gravité : " + xpathGet(crise, "Gravite"));
                System.out.println("Type : " + xpathGet(crise, "Type"));

                // CRP optionnel directement avec getElementsByTagName
                NodeList crpNodes = crise.getElementsByTagName("CRP");
                if (crpNodes.getLength() > 0) {
                    System.out.println("CRP : " + crpNodes.item(0).getTextContent());
                }

                System.out.println("Traitement : " + xpathGet(crise, "Traitement"));
                System.out.println("Durée hospitalisation : " + xpathGet(crise, "DureeHospitalisation"));
                System.out.println("-------------------------");
            }

            // Transfusions
            System.out.println("\nTransfusions :");
            NodeList transfusions = xpathGetNodes(doc, "//Transfusion");
            for (int i = 0; i < transfusions.getLength(); i++) {
                Element transf = (Element) transfusions.item(i);
                System.out.println("ID : " + transf.getAttribute("id"));
                System.out.println("Début : " + xpathGet(transf, "Debut"));
                System.out.println("Fréquence : " + xpathGet(transf, "Frequence"));
                System.out.println("Technique : " + xpathGet(transf, "Technique"));
                System.out.println("Lieu : " + xpathGet(transf, "Lieu"));
                System.out.println("Observation : " + xpathGet(transf, "Observation"));
                System.out.println("-------------------------");
            }

            // Médecins
            System.out.println("\nMédecins :");
            NodeList medecins = xpathGetNodes(doc, "//Medecin");
            for (int i = 0; i < medecins.getLength(); i++) {
                Element med = (Element) medecins.item(i);
                System.out.println("ID : " + med.getAttribute("id"));
                System.out.println("Nom : " + xpathGet(med, "Nom"));
                System.out.println("Prénom : " + xpathGet(med, "Prenom"));
                System.out.println("Spécialité : " + xpathGet(med, "Specialite"));
                System.out.println("-------------------------");
            }

        } catch (Exception e) {
            System.out.println("Erreur XPath : " + e.getMessage());
        }
    }

    // ===== Méthodes utilitaires XPath =====

    private static String xpathGet(Document doc, String expr) throws XPathExpressionException {
        return XPathFactory.newInstance().newXPath().compile(expr).evaluate(doc);
    }

    private static String xpathGet(Element element, String expr) throws XPathExpressionException {
        return XPathFactory.newInstance().newXPath().compile(expr).evaluate(element);
    }

    private static NodeList xpathGetNodes(Document doc, String expr) throws XPathExpressionException {
        return (NodeList) XPathFactory.newInstance().newXPath().compile(expr).evaluate(doc, XPathConstants.NODESET);
    }

}






