package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.humbleui.jwm.MouseButton;
import io.github.humbleui.skija.*;
import lombok.Getter;
import misc.CoordinateSystem2d;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;
import panels.PanelLog;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static app.Colors.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class Task {

    public static final String TASK_TEXT = """
            ПОСТАНОВКА ЗАДАЧИ:
            На плоскости задано множество точек.
            Найти окружность наименьшей площади,
            внутри которой находятся все точки множества.
            Если таких окружностей несколько, найти любую.
            В качестве ответа нарисовать найденную окружность.""";


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

    /**
     * Порядок разделителя сетки, т.е. раз в сколько отсечек
     * будет нарисована увеличенная
     */
    private static final int DELIMITER_ORDER = 10;

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

    /**
     * Флаг, решена ли задача
     */
    private boolean solved;

    /**
     * Рисование задачи
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */

    /**
     * Рисование сетки
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void renderGrid(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем область рисования
        canvas.save();
        // получаем ширину штриха(т.е. по факту толщину линии)
        float strokeWidth = 0.03f / (float) ownCS.getSimilarity(windowCS).y + 0.5f;
        // создаём перо соответствующей толщины
        try (var paint = new Paint().setMode(PaintMode.STROKE).setStrokeWidth(strokeWidth).setColor(TASK_GRID_COLOR)) {
            // перебираем все целочисленные отсчёты нашей СК по оси X
            for (int i = (int) (ownCS.getMin().x); i <= (int) (ownCS.getMax().x); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(i, 0, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % DELIMITER_ORDER == 0 ? 5 : 2;
                // рисуем вертикальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y + strokeHeight, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y - strokeHeight, paint);
            }
            // перебираем все целочисленные отсчёты нашей СК по оси Y
            for (int i = (int) (ownCS.getMin().y); i <= (int) (ownCS.getMax().y); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(0, i, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % 10 == 0 ? 5 : 2;
                // рисуем горизонтальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x + strokeHeight, windowPos.y, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x - strokeHeight, windowPos.y, paint);
            }
        }
        // восстанавливаем область рисования
        canvas.restore();
    }

    /**
     * Получить положение курсора мыши в СК задачи
     *
     * @param x        координата X курсора
     * @param y        координата Y курсора
     * @param windowCS СК окна
     * @return вещественный вектор положения в СК задачи
     */
    @JsonIgnore
    public Vector2d getRealPos(int x, int y, CoordinateSystem2i windowCS) {
        return ownCS.getCoords(x, y, windowCS);
    }

    /**
     * Составление массива отрезков для рисования окружности в координатах окна
     * У движка есть готовый метод рисования набора отрезков canvas.drawLines(). Этому методу передать массив вещественных чисел float размером в четыре раза большим, чем кол-во линий. В этом массиве все данные идут подряд: сначала x координата первой точки, потом y координата, потом x координата второй точки, потом y координата, следующие четыре элемента точно также описывают второй отрезок и т.д.
     *
     * @param centre центр окружности
     * @param rad    радиус
     * @return набор точек окружности
     */
    public float[] arrCircle(Vector2d centre, double rad) {
        // радиус вдоль оси x
        float radX = (float) (rad);
        // радиус вдоль оси y
        float radY = (float) (rad);
        // кол-во отсчётов цикла
        int loopCnt = 100;
        // создаём массив координат опорных точек
        float[] points = new float[loopCnt * 4];
        // запускаем цикл
        for (int i = 0; i < loopCnt; i++) {
            // координаты первой точки в СК окна
            double tmpXold = centre.x + radX * Math.cos(2 * Math.PI / loopCnt * i);
            double tmpYold = centre.y + radY * Math.sin(2 * Math.PI / loopCnt * i);
            Vector2i tmp = lastWindowCS.getCoords(tmpXold, tmpYold, ownCS);
            // записываем x
            points[i * 4] = (float) tmp.x;
            // записываем y
            points[i * 4 + 1] = lastWindowCS.getMax().y - (float) tmp.y;
            // координаты второй точки в СК окна
            tmp = lastWindowCS.getCoords(centre.x + radX * Math.cos(2 * Math.PI / loopCnt * (i + 1)), centre.y + radY * Math.sin(2 * Math.PI / loopCnt * (i + 1)), ownCS);
            // записываем x
            points[i * 4 + 2] = (float) tmp.x;
            // записываем y
            points[i * 4 + 3] = lastWindowCS.getMax().y - (float) tmp.y;
        }
        return points;
    }

    /**
     * Добавить окружность
     *
     * @param center положение центра
     * @param radius радиус
     */
    public void addCircle(Vector2d center, double radius) {
        solved = false;
        Circle newCircle = new Circle(center, radius);
        //circles.add(newCircle);
        PanelLog.info("окружность " + newCircle + " добавлена в задачу");
    }

    /**
     * Добавить случайные окружности
     *
     * @param cnt кол-во случайных окружностей
     */
    public void addRandomCircles(int cnt) {
        // повторяем заданное количество раз
        for (int i = 0; i < cnt; i++) {
            // получаем случайные координаты центра
            Vector2d pos = ownCS.getRandomCoords();
            //получаем случайный радиус
            double tmpR = ThreadLocalRandom.current().nextDouble(0, Math.min(ownCS.getSize().x, ownCS.getSize().y) / 2);
            addCircle(pos, tmpR);
        }
    }

    /**
     * Рисование курсора мыши
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     * @param font     шрифт
     * @param pos      положение курсора мыши
     */
    public void paintMouse(Canvas canvas, CoordinateSystem2i windowCS, Font font, Vector2i pos) {
        // создаём перо
        try (var paint = new Paint().setColor(TASK_GRID_COLOR)) {
            // сохраняем область рисования
            canvas.save();
            // рисуем перекрестие
            canvas.drawRect(Rect.makeXYWH(0, pos.y - 1, windowCS.getSize().x, 2), paint);
            canvas.drawRect(Rect.makeXYWH(pos.x - 1, 0, 2, windowCS.getSize().y), paint);
            // смещаемся немного для красивого вывода текста
            canvas.translate(pos.x + 3, pos.y - 5);
            // положение курсора в пространстве задачи
            Vector2d realPos = getRealPos(pos.x, pos.y, lastWindowCS);
            // выводим координаты
            canvas.drawString(realPos.toString(), 0, 0, font, paint);
            // восстанавливаем область рисования
            canvas.restore();
        }
    }

    /**
     * Рисование задачи
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    private void renderTask(Canvas canvas, CoordinateSystem2i windowCS) {
        canvas.save();
        // создаём перо
        try (var paint = new Paint()) {
            for (Point p : points) {
                paint.setColor(p.getColor());
                // y-координату разворачиваем, потому что у СК окна ось y направлена вниз,
                // а в классическом представлении - вверх
                Vector2i windowPos = windowCS.getCoords(p.pos.x, p.pos.y, ownCS);
                // рисуем точку
                canvas.drawRect(Rect.makeXYWH(windowPos.x - POINT_SIZE, windowPos.y - POINT_SIZE, POINT_SIZE * 2, POINT_SIZE * 2), paint);
            }

        }
        canvas.restore();
    }


    /**
     * Рисование
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // Сохраняем последнюю СК
        lastWindowCS = windowCS;
        // рисуем координатную сетку
        renderGrid(canvas, lastWindowCS);
        // рисуем задачу
        renderTask(canvas, windowCS);
    }

    /**
     * коэффициент колёсика мыши
     */
    private static final float WHEEL_SENSITIVE = 0.001f;

    /**
     * Масштабирование области просмотра задачи
     *
     * @param delta  прокрутка колеса
     * @param center центр масштабирования
     */
    public void scale(float delta, Vector2i center) {
        if (lastWindowCS == null) return;
        // получаем координаты центра масштабирования в СК задачи
        Vector2d realCenter = ownCS.getCoords(center, lastWindowCS);
        // выполняем масштабирование
        ownCS.scale(1 + delta * WHEEL_SENSITIVE, realCenter);
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

        addPoint(taskPos, Point.PointSet.FIRST_SET);
        // если правая, то во второе

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

            addPoint(pos, Point.PointSet.FIRST_SET);

        }
    }

    /**
     * Добавить точку
     *
     * @param pos      положение
     * @param pointSet множество
     */
    public void addPoint(Vector2d pos, Point.PointSet pointSet) {
        solved = false;
        Point newPoint = new Point(pos, pointSet);
        points.add(newPoint);
        PanelLog.info("точка " + newPoint + " добавлена в " + newPoint.getSetName());
    }

    /**
     * Очистить задачу
     */
    public void clear() {
        points.clear();
        solved = false;
    }

    /**
     * Решить задачу
     */

    public void solve() {

        Point centr;
        double rad = 10000000;
        // перебираем пары точек
        for (int i = 0; i < points.size(); i++) {
            Point cent = points.get(i);
            double dist = 0;
            for (int j = i + 1; j < points.size(); j++) {
                Point a = points.get(i);
                if (Math.sqrt((cent.pos.x - a.pos.x) * (cent.pos.x - a.pos.x) + (cent.pos.y - a.pos.y) * (cent.pos.y - a.pos.y)) > dist) {
                    dist = (cent.pos.x - a.pos.x) * (cent.pos.x - a.pos.x) + (cent.pos.y - a.pos.y) * (cent.pos.y - a.pos.y);
                    centr = cent;
                }
            }
            if (dist < rad) rad = dist;
        }


        // задача решена
        solved = true;
    }


    /**
     * Отмена решения задачи
     */
    public void cancel() {
        solved = false;
    }

    /**
     * проверка, решена ли задача
     *
     * @return флаг
     */
    public boolean isSolved() {
        return solved;
    }

    /**
     * Список точек в пересечении
     */


    /**
     * Клик мыши по пространству задачи
     *
     * @param pos         положение мыши
     * @param mouseButton кнопка мыши
     */


}