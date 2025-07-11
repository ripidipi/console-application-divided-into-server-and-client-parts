package commands;

import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import io.DistributionOfTheOutputStream;
import storage.Authentication;
import storage.Logging;

import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class CheckIsWithId implements Command<Integer> {

    private static final ReentrantLock lock = new ReentrantLock();
    /**
     * Validates and parses the ID input.
     * This method checks if the provided ID exists in the collection of study groups. If the ID is valid and exists,
     * it returns the parsed ID. Otherwise, it throws a runtime exception.
     *
     * @param id The ID as a string to validate and parse.
     * @return The parsed ID as an Integer, if it exists in the collection.
     * @throws RuntimeException If the ID is invalid or does not exist in the collection.
     */
    static Boolean validateId(Integer id, Authentication auth) throws RuntimeException {
        lock.lock();
        try {
            String currentUser = auth.name();
            return Collection.getInstance().getCollection().stream()
                    .filter(studyGroup -> studyGroup.getId().equals(id))
                    .anyMatch(studyGroup -> studyGroup.getOwner().equals(currentUser));
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }



    public void execute(Integer arg, boolean muteMode, Authentication auth) {
        DistributionOfTheOutputStream.println(validateId(arg, auth).toString());
    }
}
