package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.humbleui.jwm.MouseButton;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Rect;
import lombok.Getter;
import misc.CoordinateSystem2d;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;
import panels.PanelLog;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    public class Task {

    public static final String TASK_TEXT = """
            ПОСТАНОВКА ЗАДАЧИ:
            Заданы два множества точек в вещественном
            пространстве. Требуется построить пересечение
            и разность этих множеств""";

        /**
         * Вещественная система координат задачи
         */
        @Getter
        private final CoordinateSystem2d ownCS;
        /**
         * Список точек
         */
        @Getter
        private final ArrayList<Point> points;
    /**
     * последняя СК окна
     */
    protected CoordinateSystem2i lastWindowCS;

    /**
     * Размер точки
     */
    private static final int POINT_SIZE = 3;

        /**
         * Задача
         *
         * @param ownCS  СК задачи
         * @param points массив точек
         */
        @JsonCreator
        public Task(
                @JsonProperty("ownCS") CoordinateSystem2d ownCS,
                @JsonProperty("points") ArrayList<Point> points
        ) {
            this.ownCS = ownCS;
            this.points = points;
        }


    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        canvas.save();
        // создаём перо
        try (var paint = new Paint()) {
            for (Point p : points) {
                // получаем цвет точки
                paint.setColor(p.getColor());
                // y-координату разворачиваем, потому что у СК окна ось y направлена вниз,
                // а в классическом представлении - вверх
                Vector2i windowPos = windowCS.getCoords(p.pos.x, p.pos.y, ownCS);
                // рисуем точку
                canvas.drawRect(Rect.makeXYWH(windowPos.x - POINT_SIZE, windowPos.y - POINT_SIZE, POINT_SIZE * 2, POINT_SIZE * 2), paint);
                // Сохраняем последнюю СК
                lastWindowCS = windowCS;
            }
        }
        canvas.restore();
    }

    /**
     * Клик мыши по пространству задачи
     *
     * @param pos         положение мыши
     * @param mouseButton кнопка мыши
     */
    public void click(Vector2i pos, MouseButton mouseButton) {
        if (lastWindowCS == null) return;
        // получаем положение на экране
        Vector2d taskPos = ownCS.getCoords(pos, lastWindowCS);
        // если левая кнопка мыши, добавляем в первое множество
        if (mouseButton.equals(MouseButton.PRIMARY)) {
            addPoint(taskPos, Point.PointSet.FIRST_SET);
            // если правая, то во второе
        } else if (mouseButton.equals(MouseButton.SECONDARY)) {
            addPoint(taskPos, Point.PointSet.SECOND_SET);
        }
    }
    /**
     * Добавить точку
     *
     * @param pos      положение
     * @param pointSet множество
     */

    /**
     * Добавить случайные точки
     *
     * @param cnt кол-во случайных точек
     */
    public void addRandomPoints(int cnt) {
        // если создавать точки с полностью случайными координатами,
        // то вероятность того, что они совпадут крайне мала
        // поэтому нужно создать вспомогательную малую целочисленную ОСК
        // для получения случайной точки мы будем запрашивать случайную
        // координату этой решётки (их всего 30х30=900).
        // после нам останется только перевести координаты на решётке
        // в координаты СК задачи
        CoordinateSystem2i addGrid = new CoordinateSystem2i(30, 30);

        // повторяем заданное количество раз
        for (int i = 0; i < cnt; i++) {
            // получаем случайные координаты на решётке
            Vector2i gridPos = addGrid.getRandomCoords();
            // получаем координаты в СК задачи
            Vector2d pos = ownCS.getCoords(gridPos, addGrid);
            // сработает примерно в половине случаев
            if (ThreadLocalRandom.current().nextBoolean())
                addPoint(pos, Point.PointSet.FIRST_SET);
            else
                addPoint(pos, Point.PointSet.SECOND_SET);
        }
    }
    public void addPoint(Vector2d pos, Point.PointSet pointSet) {
        Point newPoint = new Point(pos, pointSet);
        points.add(newPoint);
        // Добавляем в лог запись информации
        PanelLog.info("точка " + newPoint + " добавлена в " + newPoint.getSetName());
    }

    /**
     * Очистить задачу
     */
    public void clear() {
        points.clear();
    }

    /**
     * Решить задачу
     */
    public void solve() {
        PanelLog.warning("Вызван метод solve()\n Пока что решения нет");
    }
    /**
     * Отмена решения задачи
     */
    public void cancel() {

    }

    /**
     * Клик мыши по пространству задачи
     *
     * @param pos         положение мыши
     * @param mouseButton кнопка мыши
     */


}