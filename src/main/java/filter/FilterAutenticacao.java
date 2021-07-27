package filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import connection.SingleConnectionBanco;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@WebFilter(urlPatterns = {"/principal/*"})/*interceptas todas as requisicoes que vierem do projeto ou mapeamento*/
public class FilterAutenticacao implements Filter {
	
	
	private static Connection connection;
    
    public FilterAutenticacao() {
    }

	/*encerra os processos quando o servico e parado*/
	public void destroy() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*interceptas as requsicoes e as respostas no sistema*/
	/*tudo que fizer no sistema vai passar por aqui*/
	/*validacao e autenticacao*/
	/*validar e fazer redirecionamento das paginas*/
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		try {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		
		String usuarioLogado = (String) session.getAttribute("usuario");
		
		String urlParaAutenticar = req.getServletPath();/*url que esta sendo acessada*/
		
		/*validar se esta logado senao redireciona para a tela de login*/
		
		if(usuarioLogado == null && !urlParaAutenticar.equalsIgnoreCase("/principal/ServletLogin")) {/*nao esta logado*/
			
			RequestDispatcher redireciona = request.getRequestDispatcher("/index.jsp?url=" + urlParaAutenticar);
			redireciona.forward(request, response);
			return; /*para a execucao e redireciona para o login*/
			
		}else {
			chain.doFilter(request, response);
		}
		
		connection.commit();/*deu tudo certo, entao comita as alteracoes no banco de dados*/
		
		}catch (Exception e) {
		e.printStackTrace();
		
		RequestDispatcher redirecionar = request.getRequestDispatcher("erro.jsp");
		request.setAttribute("msg", e.getMessage());
		redirecionar.forward(request, response);
		
		try {
			connection.rollback();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		}
	
	}

	/*INICIA os processos ou recursos quando o sevidor sobe o projeto*/
	//iniciar a conexao no banco
	public void init(FilterConfig fConfig) throws ServletException {
		connection = SingleConnectionBanco.getConnection();
	}

}
