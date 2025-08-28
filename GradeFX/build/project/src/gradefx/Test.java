package gradefx;

import java.math.BigDecimal;

import gradefx.model.GradeSystem;
import gradefx.model.GradeSystem.Grade;
import gradefx.model.PointsSystem;
import gradefx.model.PointsSystem.BoundType;
import javafx.beans.property.SimpleObjectProperty;

public class Test {

	public static void main(String[] args) {
		PointsSystem p = new PointsSystem(new SimpleObjectProperty<>(BigDecimal.valueOf(10)),
				BoundType.MOREOREQUAL_THAN, true, GradeSystem.ONE_TO_SIX);
		p.setBoundType(BoundType.LESSOREQUAL_THAN);
		System.out.println(p);

		Grade[] grades = GradeSystem.ONE_TO_SIX.getPossibleGradesASC();
		StringBuilder string = new StringBuilder();
		for (int i = grades.length - 1; i > 0; i--) {
			string.append(p.getLowerBoundForGrade(grades[i]));
			string.append('-');
			string.append(p.getUpperBoundForGrade(grades[i]));
			string.append(" -> ");
			string.append(grades[i]);
			string.append(", ");
		}
		string.append(p.getLowerBoundForGrade(grades[0]));
		string.append('-');
		string.append(p.getUpperBoundForGrade(grades[0]));
		string.append(" -> ");
		string.append(grades[0]);
		System.out.println(string.toString());

		p.setBoundType(BoundType.MORE_THAN);

		string = new StringBuilder();
		for (int i = grades.length - 1; i > 0; i--) {
			string.append(p.getLowerBoundForGrade(grades[i]));
			string.append('-');
			string.append(p.getUpperBoundForGrade(grades[i]));
			string.append(" -> ");
			string.append(grades[i]);
			string.append(", ");
		}
		string.append(p.getLowerBoundForGrade(grades[0]));
		string.append('-');
		string.append(p.getUpperBoundForGrade(grades[0]));
		string.append(" -> ");
		string.append(grades[0]);
		System.out.println(string.toString());
	}

}
