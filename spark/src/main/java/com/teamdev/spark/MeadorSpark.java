package com.teamdev.spark;

import com.teamdev.meador.InvalidProgramException;
import com.teamdev.meador.Meador;
import com.teamdev.meador.Output;
import com.teamdev.meador.Program;

import static spark.Spark.*;

public class MeadorSpark {

    public static void main(String[] args) {
        staticFiles.location("/view");

        get("/executor", (request, response) -> {
            response.redirect("index.html");
            return null;
        });

        post("/executor", (request, response) -> {
            String code = request.raw().getParameter("program");
            Meador executor = new Meador();

            try {
                Output output = executor.execute(new Program(code));
                return output.toString();
            } catch (InvalidProgramException ipe) {
                return ipe.getMessage();
            } finally {
                response.status(200);
            }
        });
    }
}
