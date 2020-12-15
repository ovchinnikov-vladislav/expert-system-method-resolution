package ml.ovcorp;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Класс, предназначенный для вычислений метода резолюций
 */
public class ResolutionMethod {

    /**
     * Реализация метода резолюций полным перебором
     * Метод работает до тех пор, пока не будет создан пустой резольвент.
     * Метод принимает конъюниктивную нормальную форму, если термы не приведены в эту форму,то метод будет работать некорректно
     */
    public static Pair<Boolean, Set<Term>> start(List<Term> CNF) {
        Set<Term> update = new HashSet<>();

        // заполняем множество дизъюнктов
        Set<Term> current = new HashSet<>(CNF);
        boolean isFoundZero = false;
        do {
            current.addAll(update);
            update.clear();
            if (isFoundZero) {
                break;
            }
            for (Term a : current) {
                for (Term b : current) {
                    if (a == b) { // если a и b - это один и тот же терм, то пропускаем
                        continue;
                    }
                    Term resolvent = generateResolvent(a, b);
                    if (resolvent != null &&
                            !current.contains(resolvent) &&
                            !update.contains(resolvent)) {
                        System.out.println("(" + a.toString() + ") / (" + b.toString() + ") = (" + resolvent.toString() + ")" );
                        update.add(resolvent);
                        if (resolvent.getName().equals(Term.ZERO_TERM)) {
                            // Если создался пустаой дизъюнкт то завершаем работу
                            isFoundZero = true;
                            break;
                        }
                    }

                }
            }

        } while(!update.isEmpty());
        // цикл выполняется до тех пор, пока не перестанет пополнятся множество
        Pair<Boolean, Set<Term>> res = new Pair<>();
        res.set(isFoundZero, current);
        return res;
    }

    /**
     * Метод создающий резольвенту - если это возможно
     * Пусть даны два Терма:
     *      V1 = A+C+-(B)
     *      V2 = K+B
     * В данном случае, возможно создать резольвенту.
     * Тогда мы получим следующу резольвенту:
     *      V1/V2 = A+C+K
     *
     * Если не возможно создать резольвент, то тогда функция вернет NULL
     * Например, в следующем случаее создать резольвнету невозможно:
     *      V1 = A+C+-(B)
     *      V2 = K+F
     *      V1/V2 = NULL
     * Пример случая когда возникает пустой дизьюнкт:
     *   V1 = A
     *   V2 = -(A)
     *   V1/V2 = 0
     * @param a
     * @param b
     * @return
     */
    private static Term generateResolvent(Term a, Term b) {
        // Получаем списки простых высказываний
        List<Term> aSimple = a.getSimpleStatement();
        List<Term> bSimple = b.getSimpleStatement();
        boolean isCreate = false;
        // Ищем однаковые название простых высказываний, которые отличаются приминением операции
        // отрицания
        List<Term> forRemoveA = new LinkedList<>();
        List<Term> forRemoveB = new LinkedList<>();
        for (Term as : aSimple) {
            for (Term bs : bSimple) {
                if ((as.getName().equals(bs.getName())) && (as.isApplyNegation() != bs.isApplyNegation())) {
                    // Если мы нашли одинаковые простые высказыванияс, но отличные по статусу отрицания,
                    // то из них можно составить резольвент
                    isCreate = true;
                    forRemoveA.add(as);
                    forRemoveB.add(bs);
                }
            }
            if (isCreate) {
                break;
            }
        }

        if (isCreate) {
            for( Term ra : forRemoveA) {
                aSimple.remove(ra);
            }
            for (Term rb : forRemoveB) {
                bSimple.remove(rb);
            }
            // Создаем резольвент
            // Создаем множество простых высказываний
            Set<Term> terms = new HashSet<>();
            terms.addAll(aSimple);
            terms.addAll(bSimple);
            if (terms.isEmpty()) {
                // Если множество простых высказываний пусто, то мы получили нулевой дизъюнкт
                return Term.createZeroTerm();
            } else {
                simplify(terms);
                // Если множество высказываний не пусто, то создаем новый терм
                return new Term(new LinkedList<>(terms));
            }

        } else {
            // Если создать резольвент из полученной пары высказываний невозможно,
            // тогда возвращаем NULL;
            return null;
        }
    }

    /**
     * Функция выполняющая сокращения в одном дизъюнкте
     * Например, пусть дан следующий дизъюнкт:
     * A+B+C+-(A)+D
     * Данная функция уберет терм А, так как в одном дизъюнкте
     * находятся противоположные термы (А и -А):
     * B+C+D
     * @param terms
     */
    private static void simplify(Set<Term> terms) {
        List<Term> remove = new LinkedList<>();
        for (Term f : terms) {
            for (Term s : terms) {
                if (f == s) {
                    continue;
                }
                if(f.getName().equals(s.getName()) && f.isApplyNegation() != s.isApplyNegation()) {
                    remove.add(f);
                    remove.add(s);
                }
            }
        }
        for (Term t : remove) {
            terms.remove(t);
        }
    }
}
