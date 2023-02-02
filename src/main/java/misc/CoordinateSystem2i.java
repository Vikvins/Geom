package misc;

import io.github.humbleui.skija.RRect;
import io.github.humbleui.skija.Rect;

import java.util.Objects;

/**
 * Ограниченная двумерная целочисленная система координат
 */
public class CoordinateSystem2i {
    /**
     * максимальная координата
     */
    private final Vector2i max;
    /**
     * минимальная координата
     */
    private final Vector2i min;
    /**
     * размер СК
     */
    private final Vector2i size;

    /**
     * Конструктор ограниченной двумерной целочисленной системы координат
     *
     * @param minX  минимальная X координата
     * @param minY  минимальная Y координата
     * @param sizeX размер по оси X
     * @param sizeY размер по оси Y
     */
    public CoordinateSystem2i(int minX, int minY, int sizeX, int sizeY) {
        min = new Vector2i(minX, minY);
        size = new Vector2i(sizeX, sizeY);
        max = Vector2i.sum(size, min);
        max.dec();
    }

    /**
     * Конструктор ограниченной двумерной целочисленной системы координат
     *
     * @param sizeX размер по оси X
     * @param sizeY размер по оси Y
     */
    public CoordinateSystem2i(int sizeX, int sizeY) {
        this(0, 0, sizeX, sizeY);
    }

    /**
     * Получить случайные координаты внутри СК
     *
     * @return случайные координаты внутри СК
     */
    public Vector2i getRandomCoords() {
        return Vector2i.rand(min, max);
    }

    /**
     * Возвращает относительное положение вектора в СК
     *
     * @param pos положение
     * @return относительное положение
     */
    public Vector2i getRelativePos(Vector2i pos) {
        return Vector2i.subtract(pos, min);
    }

    /**
     * Получить квадрат по СК
     *
     * @return квадрат
     */
    public Rect getRect() {
        return Rect.makeXYWH(min.x, min.y, size.x, size.y);
    }

    /**
     * Получить скруглённый квадрат по СК
     *
     * @param rad радиус скругления
     * @return квадрат
     */
    public RRect getRRect(float rad) {
        return RRect.makeXYWH(min.x, min.y, size.x, size.y, rad);
    }

    /**
     * Проверить, попадают ли координаты в границы СК
     *
     * @param coords координаты вектора
     * @return флаг, попадают ли координаты в границы СК
     */
    public boolean checkCoords(Vector2i coords) {
        return checkCoords(coords.x, coords.y);
    }

    /**
     * Проверить, попадают ли координаты в границы СК
     *
     * @param x координата X
     * @param y координата Y
     * @return флаг, попадают ли координаты в границы СК
     */
    public boolean checkCoords(int x, int y) {
        return x > min.x && y > min.y && x < max.x && y < max.y;
    }

    /**
     * Получить максимальную координата
     *
     * @return максимальная координата
     */
    public Vector2i getMax() {
        return max;
    }

    /**
     * Получить минимальную координата
     *
     * @return минимальная координата
     */
    public Vector2i getMin() {
        return min;
    }

    /**
     * Получить размер СК
     *
     * @return размер СК
     */
    public Vector2i getSize() {
        return size;
    }

    /**
     * Строковое представление объекта вида:
     *
     * @return "CoordinateSystem2i{min, max}"
     */
    @Override
    public String toString() {
        return "CoordinateSystem2i{" + min + ", " + max + '}';
    }

    /**
     * Проверка двух объектов на равенство
     *
     * @param o объект, с которым сравниваем текущий
     * @return флаг, равны ли два объекта
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinateSystem2i that = (CoordinateSystem2i) o;

        if (!Objects.equals(max, that.max)) return false;
        return Objects.equals(min, that.min);
    }

    /**
     * Получить хэш-код объекта
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        int result = max != null ? max.hashCode() : 0;
        result = 31 * result + (min != null ? min.hashCode() : 0);
        return result;
    }
}