package org.openmrs.module.bacteriology.api.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bacteriology.api.BacteriologyService;
import org.openmrs.module.bacteriology.api.BacteriologyConcepts;
import org.openmrs.module.bacteriology.api.BacteriologyService;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.openmrs.module.emrapi.utils.HibernateLazyLoader;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.text.SimpleDateFormat;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BacteriologyServiceTest extends BaseModuleContextSensitiveTest {

    private BacteriologyService bacteriologyService;

    @Autowired
    private EncounterService encounterService;

    @Autowired
    private ConceptService conceptService;

    @Before
    public void setup() throws Exception {
        executeDataSet("baseBacteriologyData.xml");
        executeDataSet("specimenTestData.xml");
        bacteriologyService =  Context.getService(BacteriologyService.class);
    }

    @Test
    public void ensureEncounterIsUpdatedWithNewSpecimenObs() throws Exception {
        Encounter encounter = encounterService.getEncounter(3);

        Resource etRequest = new ClassPathResource("encounterTransactionWithBasicSpecimen.json");
        EncounterTransaction encounterTransaction = new ObjectMapper().readValue(etRequest.getInputStream(), EncounterTransaction.class);
        bacteriologyService.updateEncounter(encounter, encounterTransaction);

        Obs specimenObs = findMember(encounter.getObsAtTopLevel(false), getConcept(BacteriologyConcepts.BACTERIOLOGY_CONCEPT_SET));
        assertNotNull(specimenObs);

        Obs specimenId = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_ID_CODE));
        assertNotNull(specimenId);
        assertEquals("SAMP1234", specimenId.getValueText());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Obs specimenDateCollected = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_COLLECTION_DATE));
        assertNotNull(specimenDateCollected);
        assertEquals("2015-09-18", sdf.format(specimenDateCollected.getValueDate()));

        Obs specimenSource = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_SAMPLE_SOURCE));
        assertNotNull(specimenSource);
        assertNotNull(specimenSource.getValueCoded());
        assertEquals(getConcept("URINE"), specimenSource.getValueCoded());
    }

    @Test
    public void ensureEncounterIsUpdatedWithExistingSpecimenObs() throws Exception {
        executeDataSet("existingSpecimenObs.xml");
        Encounter encounter = encounterService.getEncounter(102);

        Resource etRequest = new ClassPathResource("encounterTransactionWithBasicSpecimenExistingObs.json");
        EncounterTransaction encounterTransaction = new ObjectMapper().readValue(etRequest.getInputStream(), EncounterTransaction.class);
        bacteriologyService.updateEncounter(encounter, encounterTransaction);

        Obs specimenObs = findMember(encounter.getObsAtTopLevel(false), getConcept(BacteriologyConcepts.BACTERIOLOGY_CONCEPT_SET));
        assertNotNull(specimenObs);

        Obs specimenId = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_ID_CODE));
        assertNotNull(specimenId);
        assertEquals("SAMP1234", specimenId.getValueText());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Obs specimenDateCollected = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_COLLECTION_DATE));
        assertNotNull(specimenDateCollected);
        assertEquals("2015-09-18", sdf.format(specimenDateCollected.getValueDate()));

        Obs specimenSource = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_SAMPLE_SOURCE));
        assertNotNull(specimenSource);
        assertNotNull(specimenSource.getValueCoded());
        assertEquals(getConcept("URINE"), specimenSource.getValueCoded());

        Obs additionalAttributes = findMember(specimenObs.getGroupMembers(), getConcept("BACTERIOLOGY ADDITIONAL ATTRIBUTES"));
        assertNotNull(additionalAttributes);

        Obs weight = findMember(additionalAttributes.getGroupMembers(), getConcept("WEIGHT (KG)"));
        assertNotNull(weight);
        assertEquals(70, weight.getValueNumeric().intValue());

        Obs results = findMember(specimenObs.getGroupMembers(), getConcept("BACTERIOLOGY RESULTS"));
        assertNotNull(results);

        Obs weightResult = findMember(results.getGroupMembers(), getConcept("WEIGHT (KG)"));
        assertNotNull(weightResult);
        assertEquals(360, weightResult.getValueNumeric().intValue());
    }

    @Test
    public void ensureEncounterIsUpdatedWithAdditionalAttributes() throws Exception {
        Encounter encounter = encounterService.getEncounter(3);

        Resource etRequest = new ClassPathResource("encounterTransactionWithAdditionalAttr.json");
        EncounterTransaction encounterTransaction = new ObjectMapper().readValue(etRequest.getInputStream(), EncounterTransaction.class);
        bacteriologyService.updateEncounter(encounter, encounterTransaction);

        Obs specimenObs = findMember(encounter.getObsAtTopLevel(false), getConcept(BacteriologyConcepts.BACTERIOLOGY_CONCEPT_SET));
        assertNotNull(specimenObs);

        Obs specimenId = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_ID_CODE));
        assertNotNull(specimenId);
        assertEquals("SAMP1234", specimenId.getValueText());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Obs specimenDateCollected = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_COLLECTION_DATE));
        assertNotNull(specimenDateCollected);
        assertEquals("2015-09-18", sdf.format(specimenDateCollected.getValueDate()));

        Obs specimenSource = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_SAMPLE_SOURCE));
        assertNotNull(specimenSource);
        assertNotNull(specimenSource.getValueCoded());
        assertEquals(getConcept("URINE"), specimenSource.getValueCoded());

        Obs additionalAttributes = findMember(specimenObs.getGroupMembers(), getConcept("BACTERIOLOGY ADDITIONAL ATTRIBUTES"));
        assertNotNull(additionalAttributes);

        Obs weight = findMember(additionalAttributes.getGroupMembers(), getConcept("WEIGHT (KG)"));
        assertNotNull(weight);
        assertEquals(45, weight.getValueNumeric().intValue());
    }

    @Test
    public void ensureEncounterIsUpdatedWithAdditionalAttributesAndReports() throws Exception {
        Encounter encounter = encounterService.getEncounter(3);

        Resource etRequest = new ClassPathResource("encounterTransactionWithAdditionalAttrAndReports.json");
        EncounterTransaction encounterTransaction = new ObjectMapper().readValue(etRequest.getInputStream(), EncounterTransaction.class);
        bacteriologyService.updateEncounter(encounter, encounterTransaction);

        Obs specimenObs = findMember(encounter.getObsAtTopLevel(false), getConcept(BacteriologyConcepts.BACTERIOLOGY_CONCEPT_SET));
        assertNotNull(specimenObs);

        Obs specimenId = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_ID_CODE));
        assertNotNull(specimenId);
        assertEquals("SAMP1234", specimenId.getValueText());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Obs specimenDateCollected = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_COLLECTION_DATE));
        assertNotNull(specimenDateCollected);
        assertEquals("2015-09-18", sdf.format(specimenDateCollected.getValueDate()));

        Obs specimenSource = findMember(specimenObs.getGroupMembers(), getConcept(BacteriologyConcepts.SPECIMEN_SAMPLE_SOURCE));
        assertNotNull(specimenSource);
        assertNotNull(specimenSource.getValueCoded());
        assertEquals(getConcept("URINE"), specimenSource.getValueCoded());

        Obs additionalAttributes = findMember(specimenObs.getGroupMembers(), getConcept("BACTERIOLOGY ADDITIONAL ATTRIBUTES"));
        assertNotNull(additionalAttributes);

        Obs weight = findMember(additionalAttributes.getGroupMembers(), getConcept("WEIGHT (KG)"));
        assertNotNull(weight);
        assertEquals(45, weight.getValueNumeric().intValue());

        Obs results = findMember(specimenObs.getGroupMembers(), getConcept("BACTERIOLOGY RESULTS"));
        assertNotNull(results);

        Obs weightResult = findMember(results.getGroupMembers(), getConcept("WEIGHT (KG)"));
        assertNotNull(weightResult);
        assertEquals(90, weightResult.getValueNumeric().intValue());
    }

    private Concept getConcept(String name) {
        Concept concept = conceptService.getConceptByName(name);
        return new HibernateLazyLoader().load(concept);
    }

    private Obs findMember(Set<Obs> obsList, Concept concept) {
        for (Obs candidate : obsList) {
            candidate = new HibernateLazyLoader().load(candidate);
            if (candidate.getConcept().equals(concept)) {
                return candidate;
            }
        }
        return null;
    }


}