package main.java.csye6200.controllers;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import main.java.csye6200.dao.ReportDAO;
import main.java.csye6200.dao.TransactionHistoryDAO;
import main.java.csye6200.dao.BudgetDAOImpl;
import main.java.csye6200.dao.DatabaseConnect;
import main.java.csye6200.models.YearlyReport;
import main.java.csye6200.utils.SessionManager;
import main.java.csye6200.models.MonthlySpending;
import main.java.csye6200.models.Transaction;
import main.java.csye6200.models.TransactionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;

public class SummaryController {

	    @FXML
	    private PieChart remainingBudgetPieChart;
	 

	    @FXML
	    private BarChart<String, Number> incomeExpenseBarChart;
	    
	    @FXML
	    private BarChart<String, Number> budgetVsActualBarChart; 

	    @FXML
	    private Label totalSavingsLabel;
	    @FXML
	    private Label remainingBudgetLabel;
	    @FXML
	    private Label expenseToIncomeRatioLabel;
	    
	    @FXML
	    private PieChart budgetUtilizationPieChart;

	    private TransactionHistoryDAO transactionHistoryDAO;
	    private BudgetDAOImpl budgetDAO;
	    


	    @FXML
	    public void initialize() throws ClassNotFoundException, SQLException {
	    	
	    	try (Connection con = new DatabaseConnect().getConnection()) {
	            // Initialize DAO objects with shared connection
	            transactionHistoryDAO = new TransactionHistoryDAO(con);
	            budgetDAO = new BudgetDAOImpl(new DatabaseConnect());
	            
	            // Get logged-in user ID from the session
//	            String userId = SessionManager.getInstance().getUserId();

	            // Step 1: Aggregate data for Financial Summary (Income, Expenses, Remaining Budget)
	            loadFinancialSummary(con);
//	            loadFinancialSummary(con, userId);

	            // Step 2: Monthly Spending data already implemented (reuse the PieChart from ReportsController)
	            // Step 3: Yearly Overview data already implemented (reuse the BarChart from ReportsController)
	        } catch (SQLException | ClassNotFoundException e) {
	            e.printStackTrace();
	        }

	    }
	    
	  
//	    private void loadFinancialSummary(Connection con, String userId) {
	    private void loadFinancialSummary(Connection con) throws ClassNotFoundException {
	        double totalIncome = 0;
	        double totalExpenses = 0;
	        double totalBudget = 0;
	        double totalRemaining = 0;

	        try {
	            // Fetch all transactions and calculate total income and expenses dynamically across all months
	            List<Transaction> transactions = transactionHistoryDAO.getAllTransactions();
	            for (Transaction transaction : transactions) {
	                if (transaction.getType() == TransactionType.INCOME) {
	                    totalIncome += transaction.getAmount();
	                } else if (transaction.getType() == TransactionType.EXPENSE) {
	                    totalExpenses += transaction.getAmount();
	                }
	            }

	            // Fetch budget details across all months and calculate total budget and remaining amount
	            try (ResultSet budgetRs = budgetDAO.getAllBudgetDetails()) { // Fetching all budget details
	                while (budgetRs.next()) {
	                    totalBudget += budgetRs.getDouble("amount");
	                    totalRemaining += budgetRs.getDouble("remaining_amount");
	                }
	            }

	            // Set labels with calculated data
	            totalSavingsLabel.setText("Total Savings: $" + (totalIncome - totalExpenses));
	            remainingBudgetLabel.setText("Remaining Budget: $" + (totalRemaining));

	            // Budget Utilization (Percentage of Budget Spent)
	            double utilization = (totalBudget > 0) ? (totalExpenses / totalBudget) : 0;
	            updateBudgetUtilizationPieChart(utilization);

	            // Expense to Income Ratio (Percentage of income spent on expenses)
	            double expenseToIncomeRatio = (totalIncome > 0) ? (totalExpenses / totalIncome) * 100 : 0;
	            expenseToIncomeRatioLabel.setText("Expense to Income Ratio: " + String.format("%.2f", expenseToIncomeRatio) + "%");

	            
	            // Visualize Income vs Expenses
	            visualizeIncomeVsExpenses(totalIncome, totalExpenses);
	            
	            // Budget vs Actual
	            visualizeBudgetVsActual(totalBudget, totalExpenses);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	    }
	    
	    private void updateBudgetUtilizationPieChart(double utilization) {
	        // Budget Utilization Pie Chart
	        PieChart.Data spentData = new PieChart.Data("Spent", utilization * 100);  // Percentage spent
	        PieChart.Data remainingData = new PieChart.Data("Remaining", (1 - utilization) * 100);  // Remaining percentage

	        budgetUtilizationPieChart.getData().clear();
	        budgetUtilizationPieChart.getData().addAll(spentData, remainingData);

	    }
	    
	    private void visualizeIncomeVsExpenses(double totalIncome, double totalExpenses) {
	        // Creating data for income vs expenses
	        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
	        incomeSeries.setName("Income");
	        incomeSeries.getData().add(new XYChart.Data<>("Total", totalIncome));

	        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
	        expenseSeries.setName("Expenses");
	        expenseSeries.getData().add(new XYChart.Data<>("Total", totalExpenses));

	        // Set data to the chart
	        incomeExpenseBarChart.getData().clear();
	        incomeExpenseBarChart.getData().addAll(incomeSeries, expenseSeries);
	    }
	    
	    private void visualizeBudgetVsActual(double totalBudget, double totalExpenses) {
	        // Budget vs Actual Spending Bar Chart
	        XYChart.Series<String, Number> budgetSeries = new XYChart.Series<>();
	        budgetSeries.setName("Budget");
	        budgetSeries.getData().add(new XYChart.Data<>("Total", totalBudget));

	        XYChart.Series<String, Number> actualSeries = new XYChart.Series<>();
	        actualSeries.setName("Actual Spending");
	        actualSeries.getData().add(new XYChart.Data<>("Total", totalExpenses));

	        // Set data to the chart
	        budgetVsActualBarChart.getData().clear();
	        budgetVsActualBarChart.getData().addAll(budgetSeries, actualSeries);
	    }
}


