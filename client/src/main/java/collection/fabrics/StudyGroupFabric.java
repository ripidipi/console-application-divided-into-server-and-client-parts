package collection.fabrics;

import collection.*;
import commands.Exit;
import exceptions.*;
import io.*;
import storage.Logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static collection.StudyGroup.generateId;

public class StudyGroupFabric {

    public static StudyGroup getEmptyStudyGroup() {
        return new StudyGroup(-1, " ", new Coordinates(-1L, -1F), -1,
                FormOfEducation.FULL_TIME_EDUCATION, Semester.EIGHTH,
                new Person(" ", LocalDateTime.parse("11/11/1111 11:11:11",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), -1D, " "));
    }

    /**
     * Checks if the StudyGroup object has all required fields filled.
     *
     * @param studyGroup The StudyGroup object to check.
     * @return true if the StudyGroup is valid, false otherwise.
     */
    private static boolean isRightFill(StudyGroup studyGroup) {
        if (studyGroup == null) { return false; }
        return PersonFabric.isRightFill(studyGroup.getGroupAdmin()) && CoordinatesFabric.isRightFill(studyGroup.getCoordinates()) &&
                studyGroup.getName() != null && studyGroup.getCreationDate() != null && studyGroup.getStudentCount() != null &&
                studyGroup.getFormOfEducation() != null && studyGroup.getSemester() != null && studyGroup.getId() != null;
    }

    /**
     * Input manager for creating a StudyGroup object from user input.
     *
     * @param Arg Optional argument to specify the ID.
     * @return A new StudyGroup object created from the user input.
     */
    public static StudyGroup input(String... Arg) throws RemoveOfTheNextSymbol {
        Integer id = null;
        if (Arg.length > 0) {
            id = PrimitiveDataInput.inputFromFile("id", Arg[0], Integer.class);
        }
        DistributionOfTheOutputStream.println("Enter information about study group");
        String name = PrimitiveDataInput.input("name", String.class);
        Coordinates coordinates = CoordinatesFabric.input();
        Integer studentCount = PrimitiveDataInput.input("students count", Integer.class);
        FormOfEducation formOfEducation = EnumInput.inputFromConsole(FormOfEducation.class);
        Semester semester = EnumInput.inputFromConsole(Semester.class);
        Person groupAdmin = PersonFabric.input();
        if (id != null) {
            return new StudyGroup(id, name, coordinates, studentCount,
                    formOfEducation, semester, groupAdmin);
        }
        return new StudyGroup(name, coordinates, studentCount, formOfEducation, semester, groupAdmin);
    }

    /**
     * Input manager for creating a StudyGroup object from data read from a file.
     *
     * @param inputSplit The input data split into parts.
     * @param notAdded Flag to indicate if the ID should not be added to the list of IDs.
     * @return A new StudyGroup object created from the file data.
     */
    public static StudyGroup inputFromFile(String[] inputSplit, boolean notAdded) {
        try {
            Integer id = PrimitiveDataInput.inputFromFile("id", inputSplit[0], Integer.class);
            String name = PrimitiveDataInput.inputFromFile("name", inputSplit[1], String.class);
            Coordinates coordinates = CoordinatesFabric.inputFromFile(inputSplit[2], inputSplit[3]);
            Integer studentCount = PrimitiveDataInput.inputFromFile("students count", inputSplit[4],
                    Integer.class);
            FormOfEducation formOfEducation = EnumTransform.TransformToEnum(FormOfEducation.class, inputSplit[5]);
            Semester semester = EnumTransform.TransformToEnum(Semester.class, inputSplit[6]);
            Person groupAdmin = PersonFabric.inputFromFile(inputSplit[7], inputSplit[8], inputSplit[9], inputSplit[10]);
            return rightInisilizeStudyGroup(inputSplit, notAdded, id, name, coordinates, studentCount,
                    formOfEducation, semester, groupAdmin);
        } catch (CommandDataFromTheFileIsIncorrect e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    public static StudyGroup inputMixed(String[] inputSplit, boolean notAdded, boolean isId) {
        try {
            int index = 1;
            Integer id;
            if (isId) {
                id = (index < inputSplit.length) ?
                        PrimitiveDataInput.inputFromFile("id", inputSplit[index++], Integer.class) :
                        PrimitiveDataInput.input("id", Integer.class);
            } else {
                id = generateId();
            }

            String name = (index < inputSplit.length) ?
                    PrimitiveDataInput.inputFromFile("name", inputSplit[index++], String.class) :
                    PrimitiveDataInput.input("name", String.class);

            Long coordX = (index < inputSplit.length) ?
                    (inputSplit[index].isBlank() ?
                            null :
                            PrimitiveDataInput.inputFromFile("x", inputSplit[index], Long.class)) :
                    PrimitiveDataInput.input("x coordinate", Long.class, false,
                            false, false, null);
                    index++;
            Float coordY = (index < inputSplit.length) ?
                    (inputSplit[index].isBlank() ?
                            null :
                            PrimitiveDataInput.inputFromFile("y", inputSplit[index], Float.class)) :
                    PrimitiveDataInput.input("y coordinate", Float.class, false,
                            false, false, null);
                    index++;
            Coordinates coordinates = new Coordinates(coordX, coordY);

            Integer studentCount = (index < inputSplit.length) ?
                    PrimitiveDataInput.inputFromFile("students count", inputSplit[index++], Integer.class) :
                    PrimitiveDataInput.input("students count", Integer.class);

            FormOfEducation formOfEducation = (index < inputSplit.length) ?
                    EnumTransform.TransformToEnum(FormOfEducation.class, inputSplit[index++]) :
                    EnumInput.inputFromConsole(FormOfEducation.class);

            Semester semester = (index < inputSplit.length) ?
                    EnumTransform.TransformToEnum(Semester.class, inputSplit[index++]) :
                    EnumInput.inputFromConsole(Semester.class);

            Person groupAdmin = PersonFabric.PersonMixedInput(inputSplit, index);

            return rightInisilizeStudyGroup(inputSplit, notAdded, id, name, coordinates, studentCount,
                    formOfEducation, semester, groupAdmin);
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    private static StudyGroup rightInisilizeStudyGroup(String[] inputSplit, boolean notAdded, Integer id, String name,
                                                       Coordinates coordinates, Integer studentCount,
                                                       FormOfEducation formOfEducation, Semester semester,
                                                       Person groupAdmin) {
        if (id != null && StudyGroup.IDs.containsKey(id) && !notAdded) {
            id = null;
        }

        StudyGroup studyGroup = new StudyGroup(id, name, coordinates, studentCount,
                formOfEducation, semester, groupAdmin);

        if (!isRightFill(studyGroup)) {
            throw new CommandDataFromTheFileIsIncorrect(String.join(",", inputSplit));
        }

        return studyGroup;
    }

    /**
     * Factory method to create a StudyGroup based on the input mode.
     *
     * @param inputMode The input mode, which can be "M" for mixed input, "F" for file input, or default for standard input.
     * @param input The input data, which can vary based on the input mode.
     * @param notAdd Flag to indicate whether the ID should not be added to the list of IDs.
     * @param isId Flag to specify if the ID should be considered during the input.
     * @return A StudyGroup object created based on the provided input and mode.
     * @throws RemoveOfTheNextSymbol If an error occurs while processing the input.
     */
    public static StudyGroup getStudyGroupFrom(String inputMode, String[] input, boolean notAdd, boolean isId)
            throws RemoveOfTheNextSymbol, IncorrectValue {
        StudyGroup studyGroup;
        if (inputMode.equalsIgnoreCase("M")) {
            studyGroup = StudyGroupFabric.inputMixed(input, notAdd, isId);
        } else if (inputMode.equalsIgnoreCase("F")) {
            studyGroup = StudyGroupFabric.inputFromFile(input, notAdd);
        } else {
            if (input.length >= 1 && !input[0].isEmpty()) {
                studyGroup = StudyGroupFabric.input(input[0]);
            } else {
                studyGroup =StudyGroupFabric.input();
            }
        }
        if (!isRightFill(studyGroup)) {
            throw new IncorrectValue(String.join(",", input));
        }
        return studyGroup;
    }


    public static StudyGroup parseStudyGroup(String arg, String inputMode, String commandName, boolean isId)
            throws InsufficientNumberOfArguments, RemoveOfTheNextSymbol, IncorrectValue {
        StudyGroup studyGroup;
        if (inputMode.equalsIgnoreCase("C")) {
            Integer id;
            if (isId) {
                id = getIdInteger(arg);
            } else
                id = PrimitiveDataInput.input("id", Integer.class);
            studyGroup = StudyGroupFabric.getStudyGroupFrom("C", new String[]{id.toString()}, false, true);
        } else {
            String[] inputSplit = arg.split(",");
            if (inputMode.equalsIgnoreCase("F") &&
                    StudyGroup.formatStudyGroupToCSV(StudyGroupFabric.getEmptyStudyGroup()).split(",").length
                            != inputSplit.length) {
                throw new InsufficientNumberOfArguments(commandName);
            }
            studyGroup = StudyGroupFabric.getStudyGroupFrom(inputMode, inputSplit, false, true);
        }
        return studyGroup;
    }

    public static Integer getIdInteger(String arg) {
        Integer id;
        id = PrimitiveDataInput.inputFromFile("id", arg, Integer.class);
        if (id == null)
            throw new IncorrectValue("id");
        return id;
    }


}
