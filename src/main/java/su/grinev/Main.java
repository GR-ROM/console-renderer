package su.grinev;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final int SCREEN_WIDTH = 80;
    private static final int SCREEN_HEIGHT = 40;

    public static void main(String[] args) {
        List<Vector3f> mesh = List.of(
                new Vector3f(-1, -1, -1),
                new Vector3f( 1, -1, -1),
                new Vector3f( 1,  1, -1),
                new Vector3f(-1,  1, -1),
                new Vector3f(-1, -1,  1),
                new Vector3f( 1, -1,  1),
                new Vector3f( 1,  1,  1),
                new Vector3f(-1,  1,  1)
        );

        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        char[][] buffer = new char[SCREEN_WIDTH][SCREEN_HEIGHT];

        Vector3f pos = new Vector3f(-3, 0, 0);
        Vector3f eye = new Vector3f(0, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0);

        Matrix4f model = new Matrix4f().identity();
        Matrix4f view = new Matrix4f().lookAt(pos, eye, up);
        Matrix4f projection = MathUtils.createProjectionMatrix(SCREEN_WIDTH / 2, SCREEN_HEIGHT, 100, 0.1f, 80);

        List<Vector2d> screenProjected = new ArrayList<>();
        Matrix4f mvp = new Matrix4f().identity();
        float rotationX = 0f;
        Vector4f translated = new Vector4f();

        while (true) {
            screenProjected.clear();
            MathUtils.updateTransformationMatrix(model, new Vector3f(0, 0, 0), rotationX, rotationX, 0, 1);

            rotationX += 0.05f;

            for (Vector3f v : mesh) {

                mvp.identity().mul(projection).mul(view).mul(model);
                translated.set(v, 1).mul(mvp);

                Vector3f ndc;
                if (translated.w != 0) {
                    ndc = new Vector3f(translated.x / translated.w, translated.y / translated.w, translated.z / translated.w);

                    float screenX = (ndc.x + 1.0f) * 0.5f * SCREEN_WIDTH;
                    float screenY = (1.0f - (ndc.y + 1.0f) * 0.5f) * SCREEN_HEIGHT;

                    screenProjected.add(new Vector2d(screenX, screenY));
                }
            }

            System.out.print("\033[H");

            for (char[] row : buffer) Arrays.fill(row, ' ');

            for (Vector2d point : screenProjected) {
                int x = (int) Math.round(point.x);
                int y = (int) Math.round(point.y);

                if (x >= 0 && x < SCREEN_WIDTH && y >= 0 && y < SCREEN_HEIGHT) {
                    buffer[x][y] = '*';
                }
            }

            for (int[] edge : edges) {
                int idx1 = edge[0];
                int idx2 = edge[1];

                if (idx1 < screenProjected.size() && idx2 < screenProjected.size()) {
                    drawLine(buffer, screenProjected.get(idx1), screenProjected.get(idx2));
                }
            }

            StringBuilder frame = new StringBuilder();
            for (int y = 0; y < SCREEN_HEIGHT; y++) {
                for (int x = 0; x < SCREEN_WIDTH; x++) {
                    frame.append(buffer[x][y]);
                }
                frame.append("\n");
            }
            System.out.print(frame);

            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void drawLine(char[][] buffer, Vector2d p1, Vector2d p2) {
        int x1 = (int) Math.round(p1.x);
        int y1 = (int) Math.round(p1.y);
        int x2 = (int) Math.round(p2.x);
        int y2 = (int) Math.round(p2.y);

        int dx = Math.abs(x2 - x1), sx = x1 < x2 ? 1 : -1;
        int dy = -Math.abs(y2 - y1), sy = y1 < y2 ? 1 : -1;
        int err = dx + dy, e2;

        while (true) {
            if (x1 >= 0 && x1 < SCREEN_WIDTH && y1 >= 0 && y1 < SCREEN_HEIGHT) {
                buffer[x1][y1] = '*';
            }
            if (x1 == x2 && y1 == y2) break;
            e2 = 2 * err;
            if (e2 >= dy) { err += dy; x1 += sx; }
            if (e2 <= dx) { err += dx; y1 += sy; }
        }
    }
}