package commands;

import storage.Logging;
import collection.Collection;
import collection.Person;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;

import java.util.Objects;
import java.util.TreeSet;

/**
 * Command that counts the number of study groups where a specified person is the admin from console.
 */
public class CountByGroupAdmin implements Helpable, Command<Person> {

    /**
     * Counts the number of study groups where the user-specified person is the admin.
     */
    public static void countByGroupAdmin(Person person) {
        TreeSet<StudyGroup> studyGroups = Collection.getInstance().getCollection();
        int adminCounter = 0;
        for (StudyGroup studyGroup : studyGroups) {
            if (Objects.equals(studyGroup.getGroupAdmin(), person)) {
                adminCounter++;
            }
        }
        DistributionOfTheOutputStream.println("The person is an admin in " + adminCounter + " groups.");
    }

    @Override
    public void execute(Person person, boolean muteMode) {
        try {
            countByGroupAdmin(person);
        } catch (InsufficientNumberOfArguments e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    @Override
    public String getHelp() {
        return "Counts the number of study groups where the specified person is the admin.";
    }
}
