package lucenforge;

import lucenforge.files.Properties;
import lucenforge.graphics.GraphicsManager;
import lucenforge.output.Monitor;
import lucenforge.physics.Physics;
import lucenforge.input.Keyboard;
import lucenforge.input.Mouse;
import lucenforge.files.Log;
import lucenforge.output.Monitors;
import lucenforge.output.Window;
import org.joml.Vector4f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Engine {

    public static void init() {
        Log.writeln(Log.SYSTEM, "LWJGL Version " + Version.getVersion() + " started!");

        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Create the window, primary for now until we can select a monitor
        int monitorIndex = Properties.getInt("window", "monitor", 0);
        Monitor monitor = Monitors.getIndex(monitorIndex);
        Monitor.set(monitor);
        Window window = new Window(monitor);
        Window.set(window);

        // Set the OpenGL viewport to match new window size on resize
        glfwSetFramebufferSizeCallback(window.id(), (win, width, height) -> {
            glViewport(0, 0, width, height);
        });

        // Set up the inputs
        Keyboard.attach(window);
        Mouse.attach(window);

        // Init physics
        Physics.init();

        // Initialize OpenGL context and renderer
        glfwMakeContextCurrent(window.id()); // Make the window's context current
        GL.createCapabilities(); // Initialize OpenGL capabilities
        GraphicsManager.init(window);
    }

    public static void start(){
        //Show the window after load
        glfwShowWindow(Window.current().id());
        glfwFocusWindow(Window.current().id());
    }

    // Frame Loop Iteration: Begins a new frame, polls for events
    public static void frameBegin(){
        frameBegin(true);
    }
    public static void frameBegin(boolean shouldClearScreen) {
        if(shouldClearScreen)
            clearScreen();

        // Update everything
        Physics.update();
        glfwPollEvents();
        Keyboard.update();
        Mouse.update();
        GraphicsManager.update();
    }
    // Frame Loop Iteration: Clears the screen and depth buffer
    public static void clearScreen(){
        glClear(GL_COLOR_BUFFER_BIT);
    }
    public static void clearDepthBuffer(){
        glClear(GL_DEPTH_BUFFER_BIT);
    }
    public static void setBackgroundColor(Vector4f color){
        glClearColor(color.x, color.y, color.z, color.w);
    }
    // Frame Loop Iteration: Ends the current frame, swaps buffers
    public static void frameEnd(){
        if(isShutdownRequested())
            shutdown();

        // swap the color buffers if it's time to render
        if(GraphicsManager.shouldRender()) {
            glfwSwapBuffers(Window.current().id());
        }
    }

    public static void shutdown(){
        // 1. Cleanup rendering stuff (while OpenGL is alive)
        GraphicsManager.cleanup();

        // 2. Free GLFW callbacks
        glfwFreeCallbacks(Window.current().id());

        // 3. Destroy the window
        glfwDestroyWindow(Window.current().id());

        // 4. Terminate GLFW itself
        glfwTerminate();

        // 5. Free the error callback
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }

        // 6. Final shutdown logging
        Log.writeln(Log.SYSTEM, "Lucenforge Engine Exit");

        // 7. Exit the program
        System.exit(0);
    }

    public static boolean isShutdownRequested() {
        return glfwWindowShouldClose(Window.current().id());
    }

    private Engine() {} // Prevent instantiation
}
