package panels;

import app.Task;
import controls.Input;
import controls.InputFactory;
import controls.Label;
import controls.MultiLineLabel;
import io.github.humbleui.jwm.*;
import io.github.humbleui.skija.Canvas;
import misc.CoordinateSystem2i;

import static app.Application.PANEL_PADDING;
import static app.Colors.FIELD_BACKGROUND_COLOR;
import static app.Colors.FIELD_TEXT_COLOR;

/**
 * Панель управления
 */
public class PanelControl extends GridPanel {
    /**
     * Текст задания
     */
    MultiLineLabel task;

    /**
     * Панель управления
     *
     * @param window     окно
     * @param drawBG     флаг, нужно ли рисовать подложку
     * @param color      цвет подложки
     * @param padding    отступы
     * @param gridWidth  кол-во ячеек сетки по ширине
     * @param gridHeight кол-во ячеек сетки по высоте
     * @param gridX      координата в сетке x
     * @param gridY      координата в сетке y
     * @param colspan    кол-во колонок, занимаемых панелью
     * @param rowspan    кол-во строк, занимаемых панелью
     */
    public PanelControl(
            Window window, boolean drawBG, int color, int padding, int gridWidth, int gridHeight,
            int gridX, int gridY, int colspan, int rowspan
    ) {
        super(window, drawBG, color, padding, gridWidth, gridHeight, gridX, gridY, colspan, rowspan);

        // задание
        task = new MultiLineLabel(
                window, false, backgroundColor, PANEL_PADDING,
                6, 7, 0, 0, 6, 2, Task.TASK_TEXT,
                false, true);

        // добавление вручную
        xLabel = new Label(window, false, backgroundColor, PANEL_PADDING,
                6, 7, 0, 2, 1, 1, "X", true, true);

        xField = InputFactory.getInput(window, false, FIELD_BACKGROUND_COLOR, PANEL_PADDING,
                6, 7, 1, 2, 2, 1, "0.0", true,
                FIELD_TEXT_COLOR);

    }

    /**
     * Обработчик событий
     *
     * @param e событие
     */
    /**
     * Обработчик событий
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        // вызываем обработчик предка
        super.accept(e);
        // передаём обработку полю ввода X
        xField.accept(e);
    }


    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */


    /**
     * заголовок для поля ввода x координаты
     */
    Label xLabel;
    /**
     * поле ввода x координаты
     */
    Input xField;
    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        task.paint(canvas, windowCS);
        xLabel.paint(canvas, windowCS);
        xField.paint(canvas, windowCS);
    }

    /**
     * Обработчик событий
     *
     * @param e событие
     */
}


}
