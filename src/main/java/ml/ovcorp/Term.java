package ml.ovcorp;

import lombok.Getter;

import java.util.*;

@Getter
public class Term {
    public static final String ZERO_TERM = "[NULL]";
    public static final String AND_OPERATION = "\u2227";
    public static final String OR_OPERATION = "\u2228";
    public static final String NOT_OPERATION = "\u00ac(";

    // Связанный список термов, которые определяют дизьюнкт
    private List<Term> disjunctions;
    // Применятся ли операция логического отрицания к дизьюнкту
    private boolean applyNegation;

    // Если данный терм описывает простое высказывание, то
    // используется это поле для определения имени простого высказывания
    // дизьюнкт не имеет имени, так как состоит постых высказываний
    private String name;

    /**
     * КОнструктор простого высказывания
     * @param name - название высказывания
     */
    public Term(String name) {
        disjunctions = Collections.emptyList();
        this.name = name;
        applyNegation = false;
    }

    /**
     * Конструктор создающий дизьюнкт из списка термов
     * @param lDisjunctions
     */
    public Term(List<Term> lDisjunctions) {
        disjunctions = new LinkedList<Term>();
        disjunctions.addAll(lDisjunctions);
        name = "";
        applyNegation = false;
    }

    private Term() {}

    /**
     * Применение операции логического отрицания к терму.
     */
    public static Term not(Term term) {
        return term.not();
    }

    private Term not() {
        Term negativeTerm = new Term();
        if (disjunctions.isEmpty()) {
            negativeTerm.disjunctions = Collections.emptyList();
            negativeTerm.name = name;

        } else {
            negativeTerm.disjunctions = new LinkedList<>();
            negativeTerm.disjunctions.addAll(disjunctions);
            negativeTerm.name = "";
        }
        negativeTerm.applyNegation = !applyNegation;
        return negativeTerm;
    }

    /**
     * Операция логического ИЛИ.
     * Создается новый терм (который является дизъюнктом)
     */
    public Term or(Term otherTerm) {
        Term tRes = new Term();
        tRes.applyNegation = false;
        tRes.name = "";
        tRes.disjunctions = new LinkedList<Term>();
        tRes.disjunctions.add(this);
        tRes.disjunctions.add(otherTerm);
        return tRes;
    }

    /**
     * Получение списка простых высказываний из терма
     */
    public List<Term> getSimpleStatement() {
        List<Term> result = new LinkedList<>();
        if (disjunctions.isEmpty()) {
            result.add(this);
        } else {
            for (Term t : disjunctions) {
                List<Term> lt = t.getSimpleStatement();
                result.addAll(lt);
            }
        }
        return result;
    }

    /**
     * Создание пустого терма. Используется для определения нулевого дизъюнкта
     */
    public static Term createZeroTerm() {
        Term term = new Term();
        term.disjunctions = Collections.emptyList();
        term.name = ZERO_TERM;
        term.applyNegation = false;
        return term;
    }

    /**
     * Печать конъюнктивной нормальной формы
     */
    public static void printCnf(List<Term> cnf) {
        System.out.println("Конъюнктивная нормальная форма:");
        StringBuilder sb = new StringBuilder();
        Iterator<Term> itr = cnf.iterator();
        int len = cnf.size();
        for (int i = 0; i < len-1; i++) {
            Term t = itr.next();
            sb.append("(")
                    .append(t.toString())
                    .append(")")
                    .append(AND_OPERATION);
        }
        Term last = itr.next();
        sb.append("(")
                .append(last.toString())
                .append(")");
        System.out.println(sb);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (applyNegation) {
            stringBuilder.append(NOT_OPERATION);
        }
        if (disjunctions.isEmpty()) {
            stringBuilder.append(name);
        } else {
            int len = disjunctions.size() - 1;
            Iterator<Term> itr = disjunctions.iterator();
            for (int i = 0; i < len; ++i) {
                Term el = itr.next();
                stringBuilder.append(el.toString());
                stringBuilder.append(OR_OPERATION);
            }
            Term endTerm = itr.next();
            stringBuilder.append(endTerm.toString());
        }

        if (applyNegation) {
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return toString().equals(obj.toString());
    }
}
