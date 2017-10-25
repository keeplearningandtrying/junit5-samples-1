package pl.codeleak.samples.junit5.basics.samples;


import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestReporter;
import pl.codeleak.samples.shared.petclinic.model.Pet;
import pl.codeleak.samples.shared.petclinic.model.Visit;
import pl.codeleak.samples.shared.petclinic.repository.Pets;

import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepeatedTestsAndTimeoutPreemptivelyTest {

    private final BadlyWrittenScheduleService testObj = new BadlyWrittenScheduleService();
    private final List<Pet> pets = new Pets().findAll();

    @RepeatedTest(12)
    void doesNotTakeMoreTimeThenNeededAndCancels(RepetitionInfo repetitionInfo, TestReporter testReporter) {
        // arrange
        int petToCheck = repetitionInfo.getCurrentRepetition() - 1;
        Pet givenPet = pets.get(petToCheck);

        testReporter.publishEntry("Making appointment for", givenPet.getName());

        // act
        Visit result = assertTimeoutPreemptively(ofSeconds(1), () -> testObj.scheduleVisit(givenPet));

        // assert
        assertEquals(givenPet.getName(), result.getPet().getName());
        assertEquals(repetitionInfo.getCurrentRepetition(), testObj.getMadeVisits());
    }
}