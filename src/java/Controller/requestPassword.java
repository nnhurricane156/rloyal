/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import JDBC.DAO;
import Model.TokenForgetPassword;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 *
 * @author tuank
 */
@WebServlet(name = "requestPassword", urlPatterns = {"/requestPassword"})
public class requestPassword extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet requestPassword</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet requestPassword at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DAO d = new DAO();
        String email = request.getParameter("email");
    //email co ton tai trong db
        User user = d.getUserbyEmail(email);
        if(user == null)
        {
            request.setAttribute("mess", "email khong ton tai");
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
            return;
        }
        
        resetService rs = new resetService();
        String token = rs.generateToken();
        String linkReset = "http://localhost:9999/RloyalPRJ/resetPassword?token=" + token;
        TokenForgetPassword newTokenForget = new TokenForgetPassword(
                user.getUser_id(), rs.expiredDateTime(), false, token);
        //send link to this email
        System.out.println(newTokenForget);
        boolean isInsert = d.insertTokenForget(newTokenForget);
        if(!isInsert)
        {
            request.setAttribute("mess", "Error");
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
            return;
        }
        boolean isSend = rs.sendEmail(email, linkReset, user.getUser_name());
        if(!isSend)
        {
            request.setAttribute("mess", "Can not send request");
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
            return;
        }
        request.setAttribute("mess", "Send request success");
        request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
        
    }    

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
