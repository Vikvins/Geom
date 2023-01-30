package misc;

/**
 * Вспомогательная библиотека
 */
public class Misc {

    /**
     * Получить цвет по компонентам
     *
     * @param a прозрачность
     * @param r красная компонента
     * @param g зелёная компонента
     * @param b синяя компонента
     * @return целове число с цветом
     */
    public static int getColor(int a, int r, int g, int b) {
        return ((a * 256 + r) * 256 + g) * 256 + b;
    }


    /**
     * Запрещаем вызов конструктора
     */
    private Misc() {
        throw new AssertionError("Вызов этого конструктора запрещён");
    }

}