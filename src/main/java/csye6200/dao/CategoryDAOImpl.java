package main.java.csye6200.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import main.java.csye6200.models.Budget;
import main.java.csye6200.models.Category;

public class CategoryDAOImpl {

	private Connection con;
	private int result;
	private ResultSet rs;
	

	public CategoryDAOImpl() throws SQLException, ClassNotFoundException {
		this.con = DatabaseConnect.getInstance().getConnection();
	}
	
	public int createCategory(Category cat) throws ClassNotFoundException {
		try {
			String query = "INSERT INTO CATEGORY VALUES (?, ?)";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, cat.getCategoryId());
			st.setString(2, cat.getCategoryName());
			result = st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result>0 ? 1:0;
		
	}
	
	public ResultSet getCategory() throws ClassNotFoundException {
		try {
			String query = "SELECT CATEGORY_NAME FROM CATEGORY";
			PreparedStatement st = con.prepareStatement(query);
			rs = st.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
		
	}
	
	public String getCategoryById(String id) throws ClassNotFoundException, SQLException {
		try {
			String query = "SELECT CATEGORY_NAME FROM CATEGORY WHERE category_id ==" + id;
			PreparedStatement st = con.prepareStatement(query);
			rs = st.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (rs.next()) {
            return rs.getString("category_name");
        }
		return "Unknown";
		
	}
	
	public Map<String, String> getCategories(String type) throws ClassNotFoundException {
        Map<String, String> categoryMap = new HashMap<>();
        System.out.println(type);
        if(type == "INCOME") {
        	try {
                String query = "SELECT CATEGORY_ID, CATEGORY_NAME FROM CATEGORY WHERE CATEGORY_NAME = 'Salary'";  // Get both ID and Name
                PreparedStatement st = con.prepareStatement(query);
                rs = st.executeQuery();
                System.out.println("resa");
                while (rs.next()) {
                	System.out.println("resa1");
                    String categoryName = rs.getString("CATEGORY_NAME");
                    String categoryId = rs.getString("CATEGORY_ID");
                    categoryMap.put(categoryName, categoryId);  // Store category name and ID
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
        try {
//            String query = "INSERT INTO category (category_id, category_name) VALUES ('ss', 'Salary')";
            String query = "SELECT CATEGORY_ID, CATEGORY_NAME FROM CATEGORY";  // Get both ID and Name
            PreparedStatement st = con.prepareStatement(query);
            rs = st.executeQuery();
            
            while (rs.next()) {
            	System.out.println("resa2");
                String categoryName = rs.getString("CATEGORY_NAME");
                String categoryId = rs.getString("CATEGORY_ID");
                categoryMap.put(categoryName, categoryId);  // Store category name and ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        }
        
        return categoryMap;
    }
	
	
	public Map<String, String> getAllCategories() throws ClassNotFoundException {
        Map<String, String> categoryMap = new HashMap<>();
        
        try {
            String query = "SELECT CATEGORY_ID, CATEGORY_NAME FROM CATEGORY";  // Get both ID and Name
            PreparedStatement st = con.prepareStatement(query);
            rs = st.executeQuery();
            
            while (rs.next()) {
                String categoryName = rs.getString("CATEGORY_NAME");
                String categoryId = rs.getString("CATEGORY_ID");
                categoryMap.put(categoryId, categoryName);  // Store category name and ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        
        return categoryMap;
    }

	public ResultSet getCategoryByName(String categoryName) throws ClassNotFoundException {
		try {
			String query = "SELECT CATEGORY_ID FROM CATEGORY WHERE CATEGORY_NAME=?";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, categoryName);
			rs = st.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
		
	}
	

}
