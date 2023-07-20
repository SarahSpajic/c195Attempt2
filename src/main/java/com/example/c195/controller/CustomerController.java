package com.example.c195.controller;

import com.example.c195.DAO.CustomerDaoImpl;
import com.example.c195.DAO.DBConnection;
import com.example.c195.DAO.FirstLevelDivisionDaoImpl;
import com.example.c195.model.Country;
import com.example.c195.model.Customer;
import com.example.c195.model.FirstLevelDivision;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class CustomerController implements Initializable {

    public TextField addressField2;
    public TextField addressField3;
    private Customer selectedCustomer;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private ComboBox<Country> countryComboBox;

    @FXML
    private ComboBox<FirstLevelDivision> stateComboBox;

    @FXML
    private int customerID;
    @FXML
    private TextField customerIdField;
    @FXML
    private int divisionID;
    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField phoneNumberField;

    private Connection connection;
    private CustomerDaoImpl customerDao;
    private FirstLevelDivisionDaoImpl firstLevelDivisionDao;
    private ObservableList<Customer> customers = FXCollections.observableArrayList();

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            this.connection = DBConnection.makeConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        firstLevelDivisionDao = new FirstLevelDivisionDaoImpl();
        firstLevelDivisionDao.setConnection(connection);
        customerDao = new CustomerDaoImpl();
            countryComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                stateComboBox.setDisable(false);
                populateDivisions(newValue.getId());
            });
        countryComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            stateComboBox.setDisable(false);
        });

        populateCountries();
    }

    /**
     * This method populates the country drop down box
     */
    private void populateCountries() {
        try {
            ObservableList<Country> countries = firstLevelDivisionDao.getAllCountries();
            countryComboBox.setItems(countries);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * This method populates the division drop down box
     */
    private void populateDivisions(int countryId) {
        try {
            Country selectedCountry = countryComboBox.getSelectionModel().getSelectedItem();
            if (selectedCountry == null) {
                return;
            }
            ObservableList<FirstLevelDivision> divisions = firstLevelDivisionDao.getDivisionsByCountryID(selectedCountry.getId());
            stateComboBox.setItems(divisions);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * This method adds a new customer to the database and the customer table
     */
    @FXML
    private void addCustomer(ActionEvent event) throws IOException {
        String name = nameField.getText();
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        Country selectedCountry = countryComboBox.getValue();
        FirstLevelDivision selectedDivision = stateComboBox.getValue();
        if (selectedCountry == null || selectedDivision == null) {
            return;
        }

        Customer newCustomer = new Customer(customerID, name, address, postalCode, phoneNumber, selectedDivision.getDivisionID());

        newCustomer.setCountryName(selectedCountry.getCountryName());

        try {
            customerDao.addCustomer(connection, newCustomer);

            dashboardController.populateTables();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }



    }
/** This method populates the customer information fields when the update customer screen is opened*/
    public void setCustomer(Customer customer) {
        this.selectedCustomer = customer;

        nameField.setText(customer.getName());
        addressField.setText(customer.getAddress());
        phoneNumberField.setText(customer.getPhoneNumber());
        postalCodeField.setText(customer.getPostalCode());
        for (Country country : countryComboBox.getItems()) {
            if (country.getId() == selectedCustomer.getCountryID()) {
                countryComboBox.setValue(country);
                break;
            }
        }

        for (FirstLevelDivision division : stateComboBox.getItems()) {
            if (division.getDivisionID() == selectedCustomer.getDivisionID()) {
                stateComboBox.setValue(division);
                break;
            }
        }
    }



    /**
     * This method updates the customer
     * to update based on the id and if a matching customer is found, that customer is removed, so this does not create duplicates.
     */
    @FXML

    private void updateCustomerAction(ActionEvent event) throws IOException {
        int customerId = selectedCustomer.getCustomerID();
        String name = nameField.getText();
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        FirstLevelDivision selectedDivision = stateComboBox.getValue();
        Country selectedCountry = countryComboBox.getValue();

        if (selectedDivision == null || selectedCustomer == null || selectedCountry == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Missing Information");
            alert.setHeaderText("Please fill in all fields");
            alert.showAndWait();
            return;
        }

        Customer updatedCustomer = new Customer(selectedCustomer.getCustomerID(), name, address, postalCode, phoneNumber, selectedDivision.getDivisionID());
        updatedCustomer.setCountryID(selectedCountry.getId());

        try {
            customerDao.updateCustomer(connection, updatedCustomer);

            dashboardController.populateTables();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


