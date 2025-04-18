package commands.interfaces;

import collection.Collection;
import collection.StudyGroup;
import io.DistributionOfTheOutputStream;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.BiPredicate;

public interface Command <T> {

    /**
     * Method for all commands. Perform main functional
     */
    void execute(T input, boolean muteMode);


    /**
     * Removes study groups from the collection that are greater or lower than the given one, based on the comparison logic.
     *
     * @param studyGroup The study group to compare with.
     * @param compare If true, removes all study groups greater than the specified one;
     *                         if false, removes all study groups lower than the specified one.
     */
    static void remove(StudyGroup studyGroup, BiPredicate<StudyGroup, StudyGroup> compare) {
        TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();

        collection.removeIf(sG -> compare.test(sG, studyGroup));
    }


}
