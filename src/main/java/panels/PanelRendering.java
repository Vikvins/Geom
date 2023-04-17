package panels;

import app.Point;
import app.Task;
import io.github.humbleui.jwm.Event;
import io.github.humbleui.jwm.EventMouseButton;
import io.github.humbleui.jwm.MouseButton;
import io.github.humbleui.jwm.Window;
import io.github.humbleui.skija.Canvas;
import misc.CoordinateSystem2d;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Панель рисования
 */
public class PanelRendering extends GridPanel {
    /**
     * Представление проблемы
     */
    public static Task task;
    ArrayList<Point> points;

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
    public PanelRendering(
            Window window, boolean drawBG, int color, int padding, int gridWidth, int gridHeight,
            int gridX, int gridY, int colspan, int rowspan
    ) {
        super(window, drawBG, color, padding, gridWidth, gridHeight, gridX, gridY, colspan, rowspan);

        // ОСК от [-10.0,-10.0] до [10.0,10.0]
        CoordinateSystem2d cs = new CoordinateSystem2d(
                new Vector2d(-10.0, -10.0), new Vector2d(10.0, 10.0)
        );

        // создаём задачу без точек
        task = new Task(cs, new ArrayList<>());
        // добавляем в нё 10 случайных
        task.addRandomPoints(10);

    }



    /**
     * Метод под рисование в конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */





    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        task.paint(canvas, windowCS);
    }

    /**
     * Обработчик событий
     * при перегрузке обязателен вызов реализации предка
     *
     * @param e событие
     */

    /**
     * Добавить точку
     *
     * @param pos      положение
     * @param pointSet множество
     */
    public void addPoint(Vector2d pos, Point.PointSet pointSet) {
        Point newPoint = new Point(pos, pointSet);
        points.add(newPoint);


    }

    /**
     * Сохранить файл
     */
    public static void save() {
        PanelLog.info("save");
    }

    /**
     * Загрузить файл
     */
    public static void load() {
        PanelLog.info("load");
    }
    /**
     * Обработчик событий
     * при перегрузке обязателен вызов реализации предка
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        // вызываем обработчик предка
        super.accept(e);
        if (e instanceof EventMouseButton ee) {
            // если последнее положение мыши сохранено и курсор был внутри
            if (lastMove != null && lastInside) {
                // если событие - нажатие мыши
                if (ee.isPressed())
                    // обрабатываем клик по задаче
                    task.click(lastWindowCS.getRelativePos(lastMove), ee.getButton());
            }
        }
    }
}