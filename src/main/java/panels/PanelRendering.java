package panels;

import app.Point;
import app.Task;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.humbleui.jwm.*;
import io.github.humbleui.skija.Canvas;
import misc.CoordinateSystem2d;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;

import java.io.File;
import java.io.IOException;
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

        /**
         * Сохранить файл
         */
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
        String path = "src/main/resources/conf.json";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File(path), task);
            PanelLog.success("Файл " + path + " успешно сохранён");
        } catch (IOException e) {
            PanelLog.error("не получилось записать файл \n" + e);
        }
    }

    /**
     * Загрузить файл
     */

    /**
     * Загружаем из файла
     *
     * @param path путь к файлу
     */
    public static void loadFromFile(String path) {
        // создаём загрузчик JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // считываем систему координат
            task = objectMapper.readValue(new File(path), Task.class);
            PanelLog.success("Файл " + path + " успешно загружен");
        } catch (IOException e) {
            PanelLog.error("Не получилось прочитать файл " + path + "\n" + e);
        }
    }

    /**
     * Загрузить файл
     */
    public static void load() {
        String path = "src/main/resources/conf.json";
        PanelLog.info("load from " + path);
        loadFromFile(path);
    }
    /**
     * Обработчик событий
     * при перегрузке обязателен вызов реализации предка
     *
     * @param e событие
     */
    /**
     * Обработчик событий
     * при перегрузке обязателен вызов реализации предка
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        // вызов обработчика предка
        super.accept(e);
        // если событие - это клик мышью
        if (e instanceof EventMouseButton ee) {
            // если последнее положение мыши сохранено и курсор был внутри
            if (lastMove != null && lastInside && ee.isPressed()) {
                // если событие - нажатие мыши
                if (ee.isPressed())
                    // обрабатываем клик по задаче
                    task.click(lastWindowCS.getRelativePos(lastMove), ee.getButton());
            }
        } else if (e instanceof EventMouseScroll ee) {
            if (lastMove != null && lastInside)
                task.scale(ee.getDeltaY(), lastWindowCS.getRelativePos(lastMove));
            window.requestFrame();
        }
    }
}