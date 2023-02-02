package app;

import io.github.humbleui.jwm.*;
import io.github.humbleui.jwm.skija.EventFrameSkija;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.RRect;
import io.github.humbleui.skija.Surface;
import misc.CoordinateSystem2i;
import misc.Misc;

import java.io.File;
import java.util.function.Consumer;

import static app.Colors.APP_BACKGROUND_COLOR;

/**
 * Класс окна приложения
 */
public class Application implements Consumer<Event> {
    /**
     * окно приложения
     */
    private final Window window;

    /**
     * Конструктор окна приложения
     */
    public Application() {
        // создаём окно
        window = App.makeWindow();
        // задаём обработчиком событий текущий объект
        window.setEventListener(this);
        // делаем окно видимым
        window.setVisible(true);

        window.setTitle("Java 2D");
        // задаём размер окна
        window.setWindowSize(900, 900);
        // задаём его положение
        window.setWindowPosition(100, 100);

        // задаём иконку
        switch (Platform.CURRENT) {
            case WINDOWS -> window.setIcon(new File("src/main/resources/windows.ico"));
            case MACOS -> window.setIcon(new File("src/main/resources/macos.icns"));
        }

        // названия слоёв, которые будем перебирать
        String[] layerNames = new String[]{
                "LayerGLSkija", "LayerRasterSkija"
        };

        // перебираем слои
        for (String layerName : layerNames) {
            String className = "io.github.humbleui.jwm.skija." + layerName;
            try {
                Layer layer = (Layer) Class.forName(className).getDeclaredConstructor().newInstance();
                window.setLayer(layer);
                break;
            } catch (Exception e) {
                System.out.println("Ошибка создания слоя " + className);
            }
        }

        // если окну не присвоен ни один из слоёв
        if (window._layer == null)
            throw new RuntimeException("Нет доступных слоёв для создания");
        // делаем окно видимым
        window.setVisible(true);
    }

    /**
     * Обработчик событий
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {
        // если событие - это закрытие окна
        if (e instanceof EventWindowClose) {
            // завершаем работу приложения
            App.terminate();
        } else if (e instanceof EventWindowCloseRequest) {
            window.close();
        } else if (e instanceof EventFrameSkija ee) {
            // получаем поверхность рисования
            Surface s = ee.getSurface();
            // очищаем её канвас заданным цветом
            paint(s.getCanvas(), new CoordinateSystem2i(s.getWidth(), s.getHeight()));
        }
    }

    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // запоминаем изменения (пока что там просто заливка цветом)
        canvas.save();
        // очищаем канвас
        canvas.clear(APP_BACKGROUND_COLOR);
        // создаём кисть
        Paint paint = new Paint();
        // задаём цвет рисования
        paint.setColor(Misc.getColor(100, 255, 255, 255));
        CoordinateSystem2i rectCS = new CoordinateSystem2i(
                windowCS.getSize().x / 3, windowCS.getSize().y / 3,
                windowCS.getSize().x / 3, windowCS.getSize().y / 3
        );
        // рисуем квадрат
        canvas.drawRRect(rectCS.getRRect(4), paint);
        // восстанавливаем состояние канваса
        canvas.restore();
    }
}