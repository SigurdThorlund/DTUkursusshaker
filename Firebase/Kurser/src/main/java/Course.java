import java.util.ArrayList;

public class Course {
    public Course(){
        // Must have a public no-argument constructor
    }
    String courseID;
    String departmentID;
    String department;
    String title;
    String language;
    double ECTS;
    int startYear;
    int endYear;
    ArrayList<String> schemePlacement;
    boolean isBoth; // er kurset både forår og efterår?
    boolean isEither; // er kurset enten forår eller efterår
    boolean isThreeWeekCourse;
    boolean isThirteenWeekCourse;
    boolean isOutsideNormalScheme;
    ArrayList<String> courseType;

    @Override
    public String toString() {
        return "Course{" +
                "courseID='" + courseID + '\'' +
                ", departmentID='" + departmentID + '\'' +
                ", department='" + department + '\'' +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", ECTS=" + ECTS +
                ", startYear=" + startYear +
                ", endYear=" + endYear +
                ", schemePlacement=" + schemePlacement +
                ", isBoth=" + isBoth +
                ", isEither=" + isEither +
                ", isThreeWeekCourse=" + isThreeWeekCourse +
                ", isThirteenWeekCourse=" + isThirteenWeekCourse +
                ", isOutsideNormalScheme=" + isOutsideNormalScheme +
                ", courseType=" + courseType +
                '}';
    }

    public Course(String courseID, String departmentID, String department, String title, String language, double ECTS, int startYear, int endYear, ArrayList<String> schemePlacement, boolean isBoth, boolean isEither, boolean isThreeWeekCourse, boolean isThirteenWeekCourse, boolean isOutsideNormalScheme, ArrayList<String> courseType) {
        this.courseID = courseID;
        this.departmentID = departmentID;
        this.department = department;
        this.title = title;
        this.language = language;
        this.ECTS = ECTS;
        this.startYear = startYear;
        this.endYear = endYear;
        this.schemePlacement = schemePlacement;
        this.isBoth = isBoth;
        this.isEither = isEither;
        this.isThreeWeekCourse = isThreeWeekCourse;
        this.isThirteenWeekCourse = isThirteenWeekCourse;
        this.isOutsideNormalScheme = isOutsideNormalScheme;
        this.courseType = courseType;
    }


}
