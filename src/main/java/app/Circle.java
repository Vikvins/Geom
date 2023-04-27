package app;

import java.util.Objects;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import misc.Vector2d;

import java.util.Objects;
public class Circle {

        /**
         * Координаты центра
         */
        public Vector2d centre;
        /**
         * радиус окружности
         */
        public double radius;

        /**
         * Конструктор окружности
         *
         * @param centre положение центра
         * @param radius радиус
         */
        @JsonCreator
        public Circle(@JsonProperty("centre") Vector2d centre, @JsonProperty("radius") double radius) {
            this.centre = centre;
            this.radius = radius;
        }

        /**
         * Получить положение центра
         *
         * @return центр
         */
        public Vector2d getCentre() {
            return centre;
        }

        /**
         * Получить радиус
         *
         * @return радиус
         */
        public double getRadius() {
            return radius;
        }

        /**
         * Строковое представление объекта
         *
         * @return строковое представление объекта
         */
        @Override
        public String toString() {
            return "Circle{" +
                    "centre=" + centre +
                    ", radius=" + String.format("%.2f", radius).replace(",", ".") +
                    '}';
        }

        /**
         * Обнулить окружность
         *
         */
/*
    public void clear() {
        this.centre = new Vector2d(0, 0);
        this.radius = 0;
    }
*/

        /**
         * Проверка двух объектов на равенство
         *
         * @param o объект, с которым сравниваем текущий
         * @return флаг, равны ли два объекта
         */
        @Override
        public boolean equals(Object o) {
            // если объект сравнивается сам с собой, тогда объекты равны
            if (this == o) return true;
            // если в аргументе передан null или классы не совпадают, тогда объекты не равны
            if (o == null || getClass() != o.getClass()) return false;
            // приводим переданный в параметрах объект к текущему классу
            Circle circle = (Circle) o;
            return (radius==circle.radius) && (Objects.equals(centre, circle.centre));
        }

        /**
         * Получить хэш-код объекта
         *
         * @return хэш-код объекта
         */
        @Override
        public int hashCode() {
            return Objects.hash(radius, centre);
        }
    }

