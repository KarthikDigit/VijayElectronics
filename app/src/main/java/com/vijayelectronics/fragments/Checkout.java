package com.vijayelectronics.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.Nullable;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;
import com.vijayelectronics.R;

import com.vijayelectronics.activities.MainActivity;
import com.vijayelectronics.adapters.CartItemsAdapter;
import com.vijayelectronics.adapters.PaymentMethodAdapter;
import com.vijayelectronics.app.App;
import com.vijayelectronics.constant.ConstantValues;
import com.vijayelectronics.customs.DialogLoader;
import com.vijayelectronics.databases.User_Cart_DB;
import com.vijayelectronics.models.coupons_model.CouponDetails;
import com.vijayelectronics.models.device_model.AppSettingsDetails;
import com.vijayelectronics.models.get_tax.GetTax;
import com.vijayelectronics.models.get_tax.Product;
import com.vijayelectronics.models.order_model.OrderDetails;
import com.vijayelectronics.models.order_model.OrderShippingMethod;
import com.vijayelectronics.models.order_model.PostOrder;
import com.vijayelectronics.models.payment_model.GetAllPaymentMethod;
import com.vijayelectronics.models.points.PointConversionModel;
import com.vijayelectronics.models.points.PointsList;
import com.vijayelectronics.models.product_model.ProductDetails;
import com.vijayelectronics.models.user_model.UserBilling;
import com.vijayelectronics.models.user_model.UserShipping;
import com.vijayelectronics.network.APIClient;
import com.vijayelectronics.utils.FacebookPixel;
import com.vijayelectronics.utils.NotificationHelper;
import com.vijayelectronics.utils.ValidateInputs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Checkout extends Fragment {
    
    View rootView;
  
    public static boolean CHECKOUT_CALL;
    
    WebView checkout_webView;
    
    PostOrder postOrder;
    OrderDetails orderDetails;
    DialogLoader dialogLoader;
    
    LinearLayout main_checkout;
    
    String ORDER_ID;
    String ORDER_RECEIVED = "order-received";
    String CHECKOUT_URL = ConstantValues.WOOCOMMERCE_URL+"android-mobile-checkout";
    
    String tax;
   
    String selectedPaymentMethod,selectedPaymentTitle;
   
    double checkoutSubtotal, checkoutTax, checkoutShipping, checkoutShippingCost, checkoutDiscount, checkoutTotal = 0;
    
    Button checkout_paypal_btn;
    CardView card_details_layout;
    ProgressDialog progressDialog;
    NestedScrollView scroll_container;
    RecyclerView checkout_items_recycler;
   // RecyclerView checkout_coupons_recycler;
    Button checkout_coupon_btn, checkout_order_btn, checkout_cancel_btn;
    ImageButton edit_billing_Btn, edit_shipping_Btn, edit_shipping_method_Btn;
    EditText checkout_coupon_code, checkout_comments, checkout_card_number, checkout_card_cvv, checkout_card_expiry;
    TextView checkout_subtotal, checkout_tax, checkout_shipping, checkout_discount,point_discount_subTotal, checkout_total, demo_coupons_text;
    TextView billing_name, billing_street, billing_address, shipping_name, shipping_street, shipping_address, shipping_method, payment_method;
    
    List<CouponDetails> couponsList;
   // List<CartDetails> checkoutItemsList;
    List<GetTax> getTaxes;
   
  //  CheckoutItemsAdapter checkoutItemsAdapter;
    
    List<GetAllPaymentMethod> paymentMethodsList;
    
    UserBilling billingAddress;
    UserShipping shippingAddress;
    List<OrderShippingMethod> shippingIDS;
    List<Product> allProducts;
   // CouponsAdapter couponsAdapter;
    OrderShippingMethod shippingMethod;
    GetTax getTaxModel;
   
    
    User_Cart_DB user_cart_db = new User_Cart_DB();
  //  User_Info_DB user_info_db = new User_Info_DB();
    
    
    CartItemsAdapter cartItemsAdapter;
    List<ProductDetails> cartItemsList;
    
    String ONE_PAGE_CHECKOUT;
    
    // Variables for Pints
    String pointsEqual,pointsEqualPrice,totalEarnedPoints;
    double pointsTotal;
    int totalPointEarned;
    CardView pointsCardLayer,pointsCardView;
    TextView points,point_discount;
    
    
    AppEventsLogger logger;
    
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.checkout, container, false);
        
        // This boolean Define this call comming from which fragmnet
        CHECKOUT_CALL = true;
        // Set the Title of Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.checkout));
        
    
        // Get the List of Cart Items from the Local Databases User_Cart_DB
        cartItemsList = ((App) getContext().getApplicationContext()).getProductDetails();
        //cartItemsList = user_cart_db.getCartItems();
    
    
        // Get the User's Info from the AppContext
        orderDetails = ((App) getContext().getApplicationContext()).getOrderDetails();
        String order = String.valueOf(orderDetails);
    
        AppSettingsDetails appSettingsDetails = ((App) getContext().getApplicationContext()).getAppSettingsDetails();
    
        if (appSettingsDetails != null) {
            ONE_PAGE_CHECKOUT = appSettingsDetails.getOnePageCheckout();
        }
        
        
        // Binding Layout Views
        checkout_order_btn = (Button) rootView.findViewById(R.id.checkout_order_btn);
        checkout_cancel_btn = (Button) rootView.findViewById(R.id.checkout_cancel_btn);
       
        edit_billing_Btn = (ImageButton) rootView.findViewById(R.id.checkout_edit_billing);
        edit_shipping_Btn = (ImageButton) rootView.findViewById(R.id.checkout_edit_shipping);
        edit_shipping_method_Btn = (ImageButton) rootView.findViewById(R.id.checkout_edit_shipping_method);
        shipping_method = (TextView) rootView.findViewById(R.id.shipping_method);
        payment_method = (TextView) rootView.findViewById(R.id.payment_method);
        checkout_subtotal = (TextView) rootView.findViewById(R.id.checkout_subtotal);
        checkout_tax = (TextView) rootView.findViewById(R.id.checkout_tax);
        checkout_shipping = (TextView) rootView.findViewById(R.id.checkout_shipping);
        checkout_discount = (TextView) rootView.findViewById(R.id.checkout_discount);
       // point_discount_subTotal = rootView.findViewById(R.id.point_discount_subTotal);
        checkout_total = (TextView) rootView.findViewById(R.id.checkout_total);
        shipping_name = (TextView) rootView.findViewById(R.id.shipping_name);
        shipping_street = (TextView) rootView.findViewById(R.id.shipping_street);
        shipping_address = (TextView) rootView.findViewById(R.id.shipping_address);
        billing_name = (TextView) rootView.findViewById(R.id.billing_name);
        billing_street = (TextView) rootView.findViewById(R.id.billing_street);
        billing_address = (TextView) rootView.findViewById(R.id.billing_address);
        demo_coupons_text = (TextView) rootView.findViewById(R.id.demo_coupons_text);
      
        checkout_comments = (EditText) rootView.findViewById(R.id.checkout_comments);
        checkout_items_recycler = (RecyclerView) rootView.findViewById(R.id.checkout_items_recycler);
        main_checkout = (LinearLayout) rootView.findViewById(R.id.main_checkout);
        
        card_details_layout = (CardView) rootView.findViewById(R.id.card_details_layout);
        checkout_paypal_btn = (Button) rootView.findViewById(R.id.checkout_paypal_btn);
        checkout_card_number = (EditText) rootView.findViewById(R.id.checkout_card_number);
        checkout_card_cvv = (EditText) rootView.findViewById(R.id.checkout_card_cvv);
        checkout_card_expiry = (EditText) rootView.findViewById(R.id.checkout_card_expiry);
        scroll_container = (NestedScrollView) rootView.findViewById(R.id.scroll_container);
        
        // points view binding
       // pointsCardView = rootView.findViewById(R.id.pointsCardView);
       // pointsCardLayer = rootView.findViewById(R.id.pointsCardLayer);
       // point_discount = rootView.findViewById(R.id.point_discount);
       // points = rootView.findViewById(R.id.points);
        
        checkout_order_btn.setEnabled(false);
        card_details_layout.setVisibility(View.GONE);
        checkout_paypal_btn.setVisibility(View.GONE);
        
        
        checkout_items_recycler.setNestedScrollingEnabled(false);
       // checkout_coupons_recycler.setNestedScrollingEnabled(false);
        
        checkout_card_expiry.setKeyListener(null);
        
        
        dialogLoader = new DialogLoader(getContext());
    
        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
        // progressDialog.setTitle(getString(R.string.processing));
        // progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        
        
        couponsList = new ArrayList<>();
        getTaxes = new ArrayList<>();
        paymentMethodsList = new ArrayList<>();
        
    
        /* Prepairing Data for Tax calculation*/
    
        if(ConstantValues.IS_ONE_PAGE_CHECKOUT_ENABLED){
        
            prepareDataForPlaceOrder();
        }
        else {
            shippingMethod = ((App) getContext().getApplicationContext()).getShippingService();
            billingAddress = ((App) getContext().getApplicationContext()).getUserDetails().getBilling();
            shippingAddress = ((App) getContext().getApplicationContext()).getUserDetails().getShipping();
        
            shippingIDS = new ArrayList<>();
            allProducts = new ArrayList<>();
            getTaxModel = new GetTax();
            Product product = new Product();
        
            getTaxModel.setShippingInfo(shippingAddress);
            getTaxModel.setBillingInfo(billingAddress);
        
        
            shippingIDS.add(shippingMethod);
        
            getTaxModel.setShippingIds(shippingIDS);
        
        
            for(int i=0;i<cartItemsList.size();i++){
            
                product.setQuantity(cartItemsList.get(i).getCustomersBasketQuantity());
                product.setProductId(cartItemsList.get(i).getId());
                product.setPrice(cartItemsList.get(i).getPrice());
                product.setTotal(cartItemsList.get(i).getTotalPrice());
                product.setVariation_id(""+cartItemsList.get(i).getSelectedVariationID());
                allProducts.add(product);
            }
        
            getTaxModel.setProducts(allProducts);
        
        
            HashMap<String, Object> params = new HashMap<>();
            params.put("billing_info", getTaxModel.getBillingInfo());
            params.put("shipping_info",getTaxModel.getShippingInfo());
            params.put("products",getTaxModel.getProducts());
            params.put("shipping_ids",getTaxModel.getShippingIds());
        
            //End
    
    
            // Request Payment Methods
            RequestPaymentMethods();
    
            /*Request Get Tax*/
    
            RequestTaxMethods(params);
    
    
            // Initialize the AddressListAdapter for RecyclerView
            cartItemsAdapter = new CartItemsAdapter(getContext(), cartItemsList,this);
    
            // Set the Adapter and LayoutManager to the RecyclerView
            checkout_items_recycler.setAdapter(cartItemsAdapter);
            checkout_items_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            cartItemsAdapter.notifyDataSetChanged();
    
    
            try {
                shipping_method.setText(shippingMethod.getMethodTitle());
                checkoutShipping = checkoutShippingCost = Double.parseDouble(shippingMethod.getTotal());
            }
            catch (Exception e){
                e.getCause();
            }
    
            // Set Billing Details
            shipping_name.setText(shippingAddress.getFirstName()+" "+shippingAddress.getLastName());
            shipping_address.setText(shippingAddress.getState()+", "+shippingAddress.getCountry());
            shipping_street.setText(shippingAddress.getAddress1());
    
            // Set Billing Details
            billing_name.setText(billingAddress.getFirstName()+" "+billingAddress.getLastName());
            billing_address.setText(billingAddress.getState()+", "+billingAddress.getCountry());
            billing_street.setText(billingAddress.getAddress1());
            
        }
        
        
        
        // Click Listner for Points
       /*
        pointsCardLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestPoints();
            }
        });*/
        
        // Handle the Click event of edit_payment_method_Btn
        payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                final PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(getContext(), paymentMethodsList);
                
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                
                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);
                
                dialog_button.setVisibility(View.GONE);
                
                dialog_title.setText(getString(R.string.payment_method));
                dialog_list.setAdapter(paymentMethodAdapter);
                
                
                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                
                
                
                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        
                        GetAllPaymentMethod userSelectedPaymentMethod = paymentMethodAdapter.getItem(position);
                        
                        payment_method.setText(userSelectedPaymentMethod.getTitle());
                        selectedPaymentMethod = userSelectedPaymentMethod.getId();
                        selectedPaymentTitle = userSelectedPaymentMethod.getTitle();
                        
                        
                        checkout_order_btn.setEnabled(true);
                        checkout_order_btn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentGreen));
    
                        checkout_order_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                prepareDataForPlaceOrder();
                            }
                        });
                        
                        scroll_container.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll_container.fullScroll(scroll_container.FOCUS_DOWN);
                            }
                        });
                        
                        
                        alertDialog.dismiss();
                        
                    }
                });
                
            }
        });
        
        
        
        
        // Handle Touch event of input_dob EditText
        checkout_card_expiry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Get Calendar instance
                    final Calendar calendar = Calendar.getInstance();
                    
                    // Initialize DateSetListener of DatePickerDialog
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            
                            // Set the selected Date Info to Calendar instance
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            
                            // Set Date Format
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy", Locale.US);
                            
                            // Set Date in input_dob EditText
                            checkout_card_expiry.setText(dateFormat.format(calendar.getTime()));
                        }
                    };
                    
                    
                    // Initialize DatePickerDialog
                    DatePickerDialog datePicker = new DatePickerDialog
                            (
                                    getContext(),
                                    date,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                            );
                    
                    // Show datePicker Dialog
                    datePicker.show();
                }
                
                return false;
            }
        });
        
        
       
        
        // Handle the Click event of edit_billing_Btn Button
        edit_billing_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                // Navigate to Billing_Address Fragment to Edit BillingAddress
                Fragment fragment = new Billing_Address();
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", true);
                fragment.setArguments(args);
                
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });
        
        
        // Handle the Click event of edit_shipping_Btn Button
        edit_shipping_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                // Navigate to Shipping_Address Fragment to Edit ShippingAddress
                Fragment fragment = new Shipping_Address();
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", true);
                fragment.setArguments(args);
                
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });
        
        
        // Handle the Click event of edit_shipping_method_Btn Button
        edit_shipping_method_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                // Navigate to Shipping_Methods Fragment to Edit ShippingMethod
                Fragment fragment = new Shipping_Methods();
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", true);
                fragment.setArguments(args);
                
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });
        
        
        
        
        // Handle the Click event of checkout_cancel_btn Button
        checkout_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel the Order and Navigate back to My_Cart Fragment
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack(getString(R.string.actionCart), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });
        
        
        // Facebook pixel
    
        /*Facebook Pixel SDK for ANalytics*/
        logger = AppEventsLogger.newLogger(getContext());
        //logSentFriendRequestEvent(logger);
    
        FacebookPixel.logCheckout(logger,getString(R.string.checkout),"","checkout",
                ConstantValues.CURRENCY_SYMBOL,""+cartItemsList.size(),checkoutTotal);
    
    
    
        return rootView;
    }
    
    
    
    //*********** Called when the fragment is no longer in use ********//
    
    @Override
    public void onDestroy() {
      //  getContext().stopService(new Intent(getContext(), PayPalService.class));
        super.onDestroy();
    }
    
    
    
    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
    }
    
    
    
    //*********** Returns Final Price of User's Cart ********//
    
    private double getProductsSubTotal() {
        
        double finalPrice = 0;
        
        for (int i=0;  i<cartItemsList.size();  i++) {
            // Add the Price of each Cart Product to finalPrice
            finalPrice += Double.parseDouble(cartItemsList.get(i).getPrice())*My_Cart.GetItemQTY(cartItemsList.get(i).getId());
        }
        
        return finalPrice;
    }
    
    
    
    //*********** Set Checkout's Subtotal, Tax, ShippingCost, Discount and Total Prices ********//
    
    private void setCheckoutTotal() {
    
        checkoutDiscount = My_Cart.cartDiscount;
        // Get Cart Total
        checkoutSubtotal = getProductsSubTotal();
        // Calculate Checkout Total
        checkoutTotal = checkoutSubtotal + checkoutTax + checkoutShipping - checkoutDiscount;
        
        // Set Checkout Details
        checkout_tax.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTax));
        checkout_shipping.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutShipping));
        checkout_discount.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutDiscount));
       // point_discount_subTotal.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(pointsTotal));
        checkout_subtotal.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutSubtotal));
        checkout_total.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTotal));
        
    }
    
    // Request for points/reward call
    
    private void RequestPoints(){
        dialogLoader.showProgressDialog();
        Call<PointConversionModel> call = APIClient.getInstance().getPoints("cool");
        
        call.enqueue(new Callback<PointConversionModel>() {
            @Override
            public void onResponse(Call<PointConversionModel> call, Response<PointConversionModel> response) {
                String URL = response.raw().request().url().toString();
                if (response.isSuccessful()) {
                    // checkoutTax = response.body();
        
                   pointsEqual = response.body().getData().getWcPointsRewardsRedeemPointsRatio().getRedeemPoint();
                   pointsEqualPrice = response.body().getData().getWcPointsRewardsRedeemPointsRatio().getEqualTo();
    
    
                    showDialogPoints();
        
                    dialogLoader.hideProgressDialog();
        
                } else {
                    // Unexpected Response from Server
                    dialogLoader.hideProgressDialog();
                    Snackbar.make(rootView, getString(R.string.tax_not_get), Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(getContext(), getString(R.string.cannot_get_payment_methods), Toast.LENGTH_SHORT).show();
                }
            
                
            }
    
            @Override
            public void onFailure(Call<PointConversionModel> call, Throwable t) {
            dialogLoader.hideProgressDialog();
            Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    // points and reWARD dialog
    
    private void showDialogPoints(){
    
       final List<PointsList> pointsLists = ((App) getContext().getApplicationContext()).getPointsList();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reward_points, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
    
        Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
        TextView totalPoints = (TextView) dialogView.findViewById(R.id.totalPoints);
        TextView pointEqual = dialogView.findViewById(R.id.pointEqual);
        // initialize totalPointEarned =0 for void in value duplication
        totalPointEarned = 0;
        totalPoints.setText(""+getTotalPoint(pointsLists));
        
        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    
        // totalPoints.setText();
        pointEqual.setText(pointsEqual+" "+getString(R.string.point_equal_to)+" "+ConstantValues.CURRENCY_SYMBOL+pointsEqualPrice);
        final EditText dialog_enter_points = dialogView.findViewById(R.id.dialog_enter_points);
    
        dialog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if(Integer.parseInt(dialog_enter_points.getText().toString()) < getTotalPoint(pointsLists)) {
                    pointsTotal = (Double.parseDouble(dialog_enter_points.getText().toString()) / Double.parseDouble(pointsEqual)) * Double.parseDouble(pointsEqualPrice);
                    // Set Checkout Total
                    setCheckoutTotal();
                    pointsCardView.setVisibility(View.VISIBLE);
                    pointsCardLayer.setVisibility(View.GONE);
                    point_discount.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(pointsTotal));
                    points.setText(dialog_enter_points.getText().toString());
                    alertDialog.dismiss();
                }
                else {
                    
                    Snackbar.make(rootView,getString(R.string.point_enter_warning),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private int getTotalPoint(List<PointsList> pointsLists){
        for (int i=0;i<pointsLists.size();i++){
            int pointValue = Integer.parseInt(pointsLists.get(i).getPoints());
            totalPointEarned += pointValue;
        }
        
        return totalPointEarned;
    }
    
    //*********** Request the Server to For Calculating Tax ********//
    
    private void RequestTaxMethods(Map<String,Object> getTax) {
        
        dialogLoader.showProgressDialog();
        String gson = new Gson().toJson(getTax);
        
        Call<Object> call = APIClient.getInstance()
                .getTax(gson);
        
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
    
                String URL = response.raw().request().url().toString();
                if (response.isSuccessful()) {
    
                   // checkoutTax = response.body();
                    
                    tax = response.body().toString();
                   // tax = response.body().
    
                    try {
                        checkoutTax = Double.parseDouble(tax);
                    }
                    catch (Exception e){
                        e.getCause();
                    }
                    // Set Checkout Total
                    setCheckoutTotal();
                    
                    dialogLoader.hideProgressDialog();
                    
                } else {
                    // Unexpected Response from Server
                    dialogLoader.hideProgressDialog();
                    Snackbar.make(rootView, getString(R.string.tax_not_get), Snackbar.LENGTH_LONG).show();
                   //Toast.makeText(getContext(), getString(R.string.cannot_get_payment_methods), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    //*********** Request the Server to get all payment methods ********//
    
    private void RequestPaymentMethods() {
        
        dialogLoader.showProgressDialog();
        
        Call<List<GetAllPaymentMethod>> call = APIClient.getInstance()
                .getAllPaymentMethods();
        
        call.enqueue(new Callback<List<GetAllPaymentMethod>>() {
            @Override
            public void onResponse(Call<List<GetAllPaymentMethod>> call, retrofit2.Response<List<GetAllPaymentMethod>> response) {
    
                if (response.isSuccessful()) {
                    
                      for (int i = 0; i < response.body().size(); i++) {
    
                          GetAllPaymentMethod paymentMethodsInfo = response.body().get(i);
    
                          if (paymentMethodsInfo.getEnabled()) {
                              paymentMethodsList.add(paymentMethodsInfo);
                          }
      
                     }
                    dialogLoader.hideProgressDialog();
        
                } else {
                    // Unexpected Response from Server
                    dialogLoader.hideProgressDialog();
                    Snackbar.make(rootView, getString(R.string.cannot_get_payment_methods), Snackbar.LENGTH_LONG).show();
                    Toast.makeText(getContext(), getString(R.string.cannot_get_payment_methods), Toast.LENGTH_SHORT).show();
                }
            }
            
            
            @Override
            public void onFailure(Call<List<GetAllPaymentMethod>> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    //*********** Place the Order on the Server ********//
    
    public void LoadCheckoutPage(String order_id) {
        dialogLoader.showProgressDialog();
        String url = CHECKOUT_URL+"?order_id="+order_id;
    
        main_checkout.setVisibility(View.GONE);
        checkout_webView.setVisibility(View.VISIBLE);
        
        Log.i("VC_Shop", "url= "+url);
        
        
        checkout_webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.i("order", "onPageStarted: url="+url);
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("VC_Shop", "onPageStarted: url= "+url);
                
                if (url.contains(ORDER_RECEIVED)) {
                    dialogLoader.hideProgressDialog();
                    view.stopLoading();
                    
                    Intent notificationIntent = new Intent(getContext(), MainActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    
                    // Order has been placed Successfully
                    NotificationHelper.showNewNotification(getContext(), notificationIntent, getString(R.string.thank_you), getString(R.string.order_placed));
                    
                    FacebookPixel.logPurchase(logger,"","Order Placed",ConstantValues.CURRENCY_SYMBOL,""+cartItemsList.size(),checkoutTotal);
                    // Clear User's Cart
                    My_Cart.ClearCart();
                    
                    // Navigate to Thank_You Fragment
                    Fragment fragment = new Thank_You();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack(getString(R.string.actionCart), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .commit();
                }
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dialogLoader.hideProgressDialog();
                Log.i("VC_Shop", "onPageFinished: url= "+url);
            }
            
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                dialogLoader.hideProgressDialog();
                Log.i("VC_Shop", "onReceivedError: error= "+error);
            }
        });
        
        checkout_webView.setVerticalScrollBarEnabled(false);
        checkout_webView.setHorizontalScrollBarEnabled(false);
        checkout_webView.setBackgroundColor(Color.TRANSPARENT);
        checkout_webView.getSettings().setJavaScriptEnabled(true);
        checkout_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        checkout_webView.loadUrl(url);
        
    }
    
    
    public void prepareDataForPlaceOrder(){
        
        // Binding Layout Views
        checkout_webView = (WebView) rootView.findViewById(R.id.checkout_webView);
    
    
        ORDER_ID = "";
        postOrder = new PostOrder();
    
        postOrder.setOrderProducts(orderDetails.getOrderProducts());
        postOrder.setOrderCoupons(orderDetails.getOrderCoupons());
    
    
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", orderDetails.getToken());
        params.put("payment_method",selectedPaymentMethod);
        params.put("payment_method_title",selectedPaymentTitle);
        params.put("billing_info",orderDetails.getBilling());
        params.put("shipping_info",orderDetails.getShipping());
        params.put("products",orderDetails.getOrderProducts());
        params.put("coupons",orderDetails.getOrderCoupons());
        params.put("shipping_ids",shippingIDS);
        params.put("customer_note",checkout_comments.getText().toString());
        params.put("customer_id",orderDetails.getCustomerId());
        params.put("one_page",ONE_PAGE_CHECKOUT);
        params.put("platform","Android");
//        params.put("redeem_points",points.getText().toString().trim());
//        params.put("redeem_price",point_discount.getText().toString().trim());
        
        dialogLoader = new DialogLoader(getContext());
        
        Gson gson = new Gson();
    
        String jsonData = gson.toJson(params);
    
        Log.i("VC_Shop", "order_json_data= "+jsonData);
    
    
        PlaceOrder(jsonData);
    }
    
    //*********** Place the Order on the Server ********//
    
    public void PlaceOrder(String jsonData) {
        
        dialogLoader.showProgressDialog();
        
        
        Call<String> call = APIClient.getInstance()
                .placeOrder
                        (
                                "cool",
                                jsonData
                        );
        
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                
                if (response.isSuccessful()) {
                    
                    if (response.body() != null  &&  !TextUtils.isEmpty(response.body())) {
                        ORDER_ID = response.body();
                        LoadCheckoutPage(ORDER_ID);
                        // Clear User's Cart
                        My_Cart.ClearCart();
                        ((MainActivity) getContext()).invalidateOptionsMenu();
                    }
                    else {
                        dialogLoader.hideProgressDialog();
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    dialogLoader.hideProgressDialog();
                    Toast.makeText(getContext(), "Error : "+response.message(), Toast.LENGTH_SHORT).show();
                }
                
            }
            
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(App.getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    //*********** Show SnackBar with given Message  ********//
    private void showSnackBarForCoupon(String msg) {
        final Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
    
    //*********** Check if the given String exists in the given List ********//
    
    private boolean isStringExistsInList(String str, List<String> stringList) {
        boolean isExists = false;
        
        for (int i=0;  i<stringList.size();  i++) {
            if (stringList.get(i).equalsIgnoreCase(str)) {
                isExists = true;
            }
        }
        
        return isExists;
    }
    
    //*********** Validate Payment Card Inputs ********//
    
    private boolean validatePaymentCard() {
        if (!ValidateInputs.isValidNumber(checkout_card_number.getText().toString().trim())) {
            checkout_card_number.setError(getString(R.string.invalid_credit_card));
            return false;
        } else if (!ValidateInputs.isValidNumber(checkout_card_cvv.getText().toString().trim())) {
            checkout_card_cvv.setError(getString(R.string.invalid_card_cvv));
            return false;
        } else if (TextUtils.isEmpty(checkout_card_expiry.getText().toString().trim())) {
            checkout_card_expiry.setError(getString(R.string.select_card_expiry));
            return false;
        } else {
            return true;
        }
    }
    
}

