import java.io.*;
import java.util.*;

class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    String uniqueID;
    String name;
    String department;
    int duration; // 4 or 5 years
    HashMap<String, Double> semesterGPAs = new HashMap<>();
    double cgpa;

    public Student(String uniqueID, String name, String department, int duration) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.department = department;
        this.duration = duration;
        this.cgpa = 0.0;
    }

    public void calculateCGPA() {
        double totalWeightedGPA = 0.0;
        double totalWeight = 0.0;

        if (duration == 4) { 
            totalWeight = 10 + 20 + 30 + 40;
            totalWeightedGPA += semesterGPAs.getOrDefault("100", 0.0) * 0.1;
            totalWeightedGPA += semesterGPAs.getOrDefault("200", 0.0) * 0.2;
            totalWeightedGPA += semesterGPAs.getOrDefault("300", 0.0) * 0.3;
            totalWeightedGPA += semesterGPAs.getOrDefault("400", 0.0) * 0.4;
        } else if (duration == 5) {
            totalWeight = 10 + 15 + 20 + 25 + 30;
            totalWeightedGPA += semesterGPAs.getOrDefault("100", 0.0) * 0.1;
            totalWeightedGPA += semesterGPAs.getOrDefault("200", 0.0) * 0.15;
            totalWeightedGPA += semesterGPAs.getOrDefault("300", 0.0) * 0.2;
            totalWeightedGPA += semesterGPAs.getOrDefault("400", 0.0) * 0.25;
            totalWeightedGPA += semesterGPAs.getOrDefault("500", 0.0) * 0.3;
        }

        this.cgpa = totalWeightedGPA / (totalWeight / 100.0);
    }

    @Override
    public String toString() {
        return "Student ID: " + uniqueID + "\n" +
                "Name: " + name + "\n" +
                "Department: " + department + "\n" +
                "CGPA: " + String.format("%.2f", cgpa) + "\n";
    }
}

public class StudentCGPACalculator {
    private static final String DATA_FILE = "students.dat";
    private static HashMap<String, Student> studentData = new HashMap<>();

    public static void main(String[] args) {
        loadStudentData();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== STUDENT CGPA CALCULATOR ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = getValidIntInput(scanner);

            switch (choice) {
                case 1 -> registerStudent(scanner);
                case 2 -> loginStudent(scanner);
                case 3 -> {
                    saveStudentData();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void registerStudent(Scanner scanner) {
        System.out.println("\n=== REGISTER STUDENT ===");
        System.out.print("Enter Unique ID: ");
        String uniqueID = scanner.nextLine().trim();
        if (studentData.containsKey(uniqueID)) {
            System.out.println("This ID already exists. Try again.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Department: ");
        String department = scanner.nextLine().trim();
        System.out.print("Enter Course Duration (4 or 5 years): ");
        int duration = getValidIntInput(scanner);

        if (duration != 4 && duration != 5) {
            System.out.println("Invalid duration. Only 4 or 5 years allowed.");
            return;
        }

        Student student = new Student(uniqueID, name, department, duration);
        studentData.put(uniqueID, student);
        System.out.println("Student registered successfully!");
    }

    private static void loginStudent(Scanner scanner) {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Enter Unique ID: ");
        String uniqueID = scanner.nextLine().trim();
        Student student = studentData.get(uniqueID);

        if (student == null) {
            System.out.println("Student not found. Try again.");
            return;
        }

        while (true) {
            System.out.println("\n=== WELCOME " + student.name.toUpperCase() + " ===");
            System.out.println("1. Add Semester Results");
            System.out.println("2. View CGPA");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");
            int choice = getValidIntInput(scanner);

            switch (choice) {
                case 1 -> addSemesterResults(scanner, student);
                case 2 -> {
                    student.calculateCGPA();
                    System.out.println(student);
                }
                case 3 -> {
                    System.out.println("Logged out successfully.");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addSemesterResults(Scanner scanner, Student student) {
        System.out.print("Enter Level (e.g., 100, 200, ...): ");
        String level = scanner.nextLine().trim();

        if (!level.matches("100|200|300|400|500")) {
            System.out.println("Invalid level. Try again.");
            return;
        }

        System.out.print("Enter the number of courses: ");
        int numCourses = getValidIntInput(scanner);
        if (numCourses <= 0) {
            System.out.println("Number of courses must be greater than 0.");
            return;
        }

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (int i = 1; i <= numCourses; i++) {
            System.out.println("Course " + i + ":");
            System.out.print("Enter Credit Load: ");
            int creditLoad = getValidIntInput(scanner);
            if (creditLoad <= 0) {
                System.out.println("Credit load must be greater than 0.");
                return;
            }

            System.out.print("Enter Grade (A, B, C, D, E, F): ");
            String grade = scanner.nextLine().toUpperCase().trim();

            int gradePoint = -1;
            switch (grade) {
                case "A" -> gradePoint = 5;
                case "B" -> gradePoint = 4;
                case "C" -> gradePoint = 3;
                case "D" -> gradePoint = 2;
                case "E" -> gradePoint = 1;
                case "F" -> gradePoint = 0;
                default -> System.out.println("Invalid grade. Try again.");
            }

            if (gradePoint == -1) {
                return;
            }

            totalPoints += gradePoint * creditLoad;
            totalCredits += creditLoad;
        }

        double gpa = totalPoints / totalCredits;
        student.semesterGPAs.put(level, gpa);
        System.out.printf("GPA for %s Level: %.2f%n", level, gpa);
    }

    private static void saveStudentData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(studentData);
        } catch (IOException e) {
            System.out.println("Error saving student data.");
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadStudentData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            studentData = (HashMap<String, Student>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        }
    }

    private static int getValidIntInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.next();
        }
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }
}