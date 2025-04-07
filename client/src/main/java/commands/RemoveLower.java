package commands;

import collection.fabrics.StudyGroupFabric;
import storage.Logging;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;
import storage.RequestPair;

/**
 * Command that removes study groups lower than a given one from the collection.
 * This command removes all study groups in the collection that have lower values than
 * the specified study group based on comparison logic defined in the class.
 */
public class RemoveLower implements Helpable, Command {

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        try {
            StudyGroup studyGroup = StudyGroupFabric.parseStudyGroup(arg, inputMode, "RemoveLower");
            return new RequestPair<>(Commands.REMOVE_LOWER, studyGroup);
        } catch (InsufficientNumberOfArguments e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    @Override
    public String getHelp() {
        return "Removes all study groups from the collection that are lower than the specified study group.";
    }
}
