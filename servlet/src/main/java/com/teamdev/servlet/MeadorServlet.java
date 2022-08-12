package com.teamdev.servlet;

import com.teamdev.meador.InvalidProgramException;
import com.teamdev.meador.Meador;
import com.teamdev.meador.Output;
import com.teamdev.meador.Program;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "executor", value = "/executor")
public class MeadorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        req.getRequestDispatcher("/WEB-INF/view/index.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var program = new Program(req.getParameter("program"));

        var executor = new Meador();
        var writer = resp.getWriter();

        try {
            Output output = executor.execute(program);

            writer.println(output.toString());
        } catch (InvalidProgramException e) {
           writer.println(e.getMessage());
        } finally {
            writer.close();
        }
    }
}

