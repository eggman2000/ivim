package com.ivim.ivim;

import static com.ivim.ivim.CoordinateConverter.fromGeodeticToUTM;
import static com.ivim.ivim.CoordinateConverter.fromUTMToGeodetic;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int PERMISO_LOCATION = 1;

    private LocationManager locationManager;
    private LatLng coord, coordenadas, latLong;
    private Marker marker;
    private ConstraintLayout mapaid,cons_check,caja_siguiente_basc,caja_mensaje_basc,caja_finalizar_basc;
    private LinearLayout caja_punto_partida, caja_edit_nombre, caja_nombre_final,
            caja_direccion, caja_giro, caja_mercado, caja_edit_tel, caja_tel_final,
            caja_recycler_marca, caja_recycler_modelo, caja_siguiente_tab, caja_x, caja_longitud,
            caja_zona,caja_instrumento, caja_edit_serie, caja_serie_final, caja_recycler_alcance,
            caja_recycler_eod,caja_recycler_minimo,caja_exactitud,caja_edit_costo,
            caja_auto_marca,caja_marca_final,caja_recycler_basculas,
            caja_costo_final,caja_edit_fecha,caja_fecha_final;
    private Fragment map;
    private int check = 0;
    private int tiempo_actualizacion = 20000;
    private SharedPreferences sharedPreferences,modeloSHER,alcanceMaxSher,eodSher,
            alcanceMinSher;
    private SharedPreferences.Editor editormodelo,editorAlcanceMax,editorEod,
            editorAlcanceMin,editor;
    private Mapa activity;
    private double latitud, longitud, altitud, latUpdate, longUpdate;
    private String direccion, nuevo_nombre, seleccion_giro,seleccion_mercado, nuevo_tel, nueva_serie,
            nuevo_costo,seleccion_instrumento,selector_modelo,checkModel,checkAlcanceMax,checkEod,checkAlcanceMin,
            seleccion_exactitud,nueva_marca,valorCheckbox,fecha_final_Str,tipo_intrumento,valcatalogo;
    private TextView puntoPartida,fecha_final, nombre, direccion_mercado, telefono, latitud_x,
            longitud_y, zona, numero_serie,costo,regresar_map, siguiente_tab,regresar_formulario,
            agregar_bascula,finalizar_reg_bascula,finalizar_no,finalizar_si,tip_model,marca_basc,listas_intrusmento,listas_exactitud,
            listas_mercado,listas_giro,regresar_otravez_formulario,agregar_otra_bascula,recycler_alcance,
            recycler_minimo,recycler_eod;
    private EditText nombre_texto, fecha, tel_texto, serie_texto,costo_texto;
    private ImageView iniciar_verificacion, guardar_nombre, cambiar_nombre, guardar_tel,
            cambiar_telefono,guardar_serie, cambiar_serie,
            guardar_costo,cambiar_costo,guardar_marca,cambiar_marca,guardar_fecha,cambiar_fecha;
    private CheckBox inicial, anual, primerSemestre, segundoSemestre, extraordinaria;
    private RecyclerView recycler_marca, recycler_modelo,recycler_numero_basc;
    private Boolean tel10;
    private ScrollView formulario_principal, formulario_bascula,almacen_basculas;
    private Spinner giros, mercado, tipoInstrumento,ClaseExactitud;
    private AdapterGiro adapterGiro;
    public ArrayList<SpinnerModel> listaGiro = new ArrayList<>();
    private AdapterMercado adapterMercado;
    public ArrayList<SpinnerModel> listaMercado = new ArrayList<>();
    private AdapterClaseExactitud adapterClaseExactitud;
    public ArrayList<SpinnerModel>listaClase=new ArrayList<>();
    private RecyclerView recyclerMarca,recyclerModelo,recyclerAlcance,recyclerEod,
            recyclerMinimo,recyclerCantidad;
    private AdapterTipoInstrumento adapterTipoInstrumento;
    public ArrayList<SpinnerModel> listaInstrumen = new ArrayList<>();
    private AdapterMarcaBasculas adapterMarcaBasculas;
    private AdapterModeloBasculas adapterModeloBasculas;


    private AdapterCantidadBasculas adapterCantidadBasculas;

    private ArrayList<ModeloRecycler> listaModelo;
    private ArrayList<MarcaRecycler> listaMarca;


    private ArrayList<CantidadBasculasRecycler>listaCantidadBasc;
    private Context context;
    public final static int WGS84 = 0;
    public final static int HAYFORD = 1;
    private final static double[] ELLIPSOID_A = {6378137.000, 6378388.000};
    private final static double[] ELLIPSOID_B = {6356752.3142449996, 6356911.946130};
    public UTMPoint p;
    public CoordinateConverter convertidor;
    public GeodeticPoint punto = new GeodeticPoint(latitud, longitud, altitud);

    private MultiAutoCompleteTextView automarca;


   private  JSONArray json_datos_bascula;
   private static String SERVIDOR_CONTROLADOR;
   private   SQLiteDatabase database,database2,database3,database4;
    private Conexion conexion1,conexion2,conexion3,conexion4;
    private Cursor cursor1,cursor2,cursor3,cursor4;
    private ArrayList<String>BASCULAS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapaid = findViewById(R.id.mapaid);
        puntoPartida = findViewById(R.id.puntoPartida);
        iniciar_verificacion = findViewById(R.id.iniciar_verificacion);
        nombre = findViewById(R.id.nombre);
        nombre_texto = findViewById(R.id.nombre_texto);
        formulario_principal = findViewById(R.id.formulario_principal);
        caja_punto_partida = findViewById(R.id.caja_punto_partida);
        caja_edit_nombre = findViewById(R.id.caja_edit_nombre);
        caja_nombre_final = findViewById(R.id.caja_nombre_final);
        guardar_nombre = findViewById(R.id.guardar_nombre);
        cambiar_nombre = findViewById(R.id.cambiar_nombre);
        caja_edit_fecha = findViewById(R.id.caja_edit_fecha);
        guardar_fecha = findViewById(R.id.guardar_fecha);
        fecha = findViewById(R.id.fecha);
        caja_fecha_final = findViewById(R.id.caja_fecha_final);
        fecha_final = findViewById(R.id.fecha_final);
        cambiar_fecha = findViewById(R.id.cambiar_fecha);


        caja_direccion = findViewById(R.id.caja_direccion);
        direccion_mercado = findViewById(R.id.direccion_mercado);
        caja_giro = findViewById(R.id.caja_giro);
        giros = findViewById(R.id.giros);
        caja_mercado = findViewById(R.id.caja_mercado);
        mercado = findViewById(R.id.mercado);
        caja_edit_tel = findViewById(R.id.caja_edit_tel);
        tel_texto = findViewById(R.id.tel_texto);
        guardar_tel = findViewById(R.id.guardar_tel);
        caja_tel_final = findViewById(R.id.caja_tel_final);
        cambiar_telefono = findViewById(R.id.cambiar_telefono);
        telefono = findViewById(R.id.telefono);
        cons_check = findViewById(R.id.cons_check);
        inicial = findViewById(R.id.inicial);
        anual = findViewById(R.id.anual);
        primerSemestre = findViewById(R.id.primerSemestre);
        segundoSemestre = findViewById(R.id.segundoSemestre);
        extraordinaria = findViewById(R.id.extraordinaria);
        activity = this;
        setListaGiro();
        setListaMercado();
        setListaInstrumen();
        setListaClase();

        listaCantidadBasc = new ArrayList<>();
        setListaCantidadBasc();
        listaModelo = new ArrayList<>();
        BASCULAS=new ArrayList<>();


        caja_siguiente_tab = findViewById(R.id.caja_siguiente_tab);
        regresar_map = findViewById(R.id.regresar_map);
        siguiente_tab = findViewById(R.id.siguiente_tab);
        formulario_bascula = findViewById(R.id.formulario_bascula);
        context = this;
        recyclerMarca = (RecyclerView) findViewById(R.id.recycler_marca);
        recyclerMarca.setLayoutManager(new LinearLayoutManager(context));
        recyclerModelo = findViewById(R.id.recycler_modelo);
        recyclerModelo.setLayoutManager(new LinearLayoutManager(context));


        recyclerCantidad=findViewById(R.id.recycler_numero_basc);
        recyclerCantidad.setLayoutManager(new LinearLayoutManager(context));


        caja_x = findViewById(R.id.caja_x);
        caja_longitud = findViewById(R.id.caja_longitud);
        latitud_x = findViewById(R.id.latitud_x);
        longitud_y = findViewById(R.id.longitud_y);
        caja_zona = findViewById(R.id.caja_zona);
        zona = findViewById(R.id.zona);
        caja_instrumento = findViewById(R.id.caja_instrumento);
        tipoInstrumento = findViewById(R.id.tipoInstrumento);
        caja_recycler_marca = findViewById(R.id.caja_recycler_marca);
        caja_recycler_modelo = findViewById(R.id.caja_recycler_modelo);
        recycler_marca = findViewById(R.id.recycler_marca);
        recycler_modelo = findViewById(R.id.recycler_modelo);
        caja_edit_serie = findViewById(R.id.caja_edit_serie);
        serie_texto = findViewById(R.id.serie_texto);
        guardar_serie = findViewById(R.id.guardar_serie);
        caja_serie_final = findViewById(R.id.caja_serie_final);
        numero_serie = findViewById(R.id.numero_serie);
        cambiar_serie = findViewById(R.id.cambiar_serie);
        caja_recycler_alcance = findViewById(R.id.caja_recycler_alcance);
        recycler_alcance = findViewById(R.id.recycler_alcance);
        caja_recycler_eod = findViewById(R.id.caja_recycler_eod);
        recycler_eod = findViewById(R.id.recycler_eod);
        caja_recycler_minimo = findViewById(R.id.caja_recycler_minimo);
        recycler_minimo = findViewById(R.id.recycler_minimo);
        caja_exactitud = findViewById(R.id.caja_exactitud);
        ClaseExactitud = findViewById(R.id.ClaseExactitud);
        caja_edit_costo = findViewById(R.id.caja_edit_costo);
        costo_texto = findViewById(R.id.costo_texto);
        guardar_costo = findViewById(R.id.guardar_costo);
        caja_costo_final = findViewById(R.id.caja_costo_final);
        costo = findViewById(R.id.costo);
        cambiar_costo = findViewById(R.id.cambiar_costo);
        caja_siguiente_basc = findViewById(R.id.caja_siguiente_basc);
        regresar_formulario = findViewById(R.id.regresar_formulario);
        agregar_bascula = findViewById(R.id.agregar_bascula);
        finalizar_reg_bascula = findViewById(R.id.finalizar_reg_bascula);
        automarca = findViewById(R.id.automarca);
        finalizar_no = findViewById(R.id.finalizar_no);
        finalizar_si = findViewById(R.id.finalizar_si);
        caja_mensaje_basc = findViewById(R.id.caja_mensaje_basc);
        caja_finalizar_basc = findViewById(R.id.caja_finalizar_basc);
        caja_auto_marca = findViewById(R.id.caja_auto_marca);
        guardar_marca = findViewById(R.id.guardar_marca);
        caja_marca_final = findViewById(R.id.caja_marca_final);
        marca_basc = findViewById(R.id.marca_basc);
        cambiar_marca = findViewById(R.id.cambiar_marca);
        SERVIDOR_CONTROLADOR = new Servidor().local;



        modeloSHER=getSharedPreferences("modelos",this.MODE_PRIVATE);
        editormodelo=modeloSHER.edit();
        editormodelo.putString("modelo","");
        editormodelo.apply();
        checkModel=modeloSHER.getString("modelo","no");

        alcanceMaxSher=getSharedPreferences("alcancesMax",this.MODE_PRIVATE);
        editorAlcanceMax=alcanceMaxSher.edit();
        editorAlcanceMax.putString("alcanceMax","");
        editorAlcanceMax.apply();
        checkAlcanceMax=alcanceMaxSher.getString("alcanceMax","no");

        eodSher=getSharedPreferences("alcancesEod",this.MODE_PRIVATE);
        editorEod=eodSher.edit();
        editorEod.putString("alcanceEod","");
        editorEod.apply();
        checkEod=eodSher.getString("alcanceEod","NO");

        alcanceMinSher=getSharedPreferences("alcancesMin",this.MODE_PRIVATE);
        editorAlcanceMin=alcanceMinSher.edit();
        editorAlcanceMin.putString("alcanceMin","");
        editorAlcanceMin.apply();
        checkAlcanceMin=alcanceMinSher.getString("alcanceMin","no");

        almacen_basculas = findViewById(R.id.almacen_basculas);
        recycler_numero_basc = findViewById(R.id.recycler_numero_basc);
        caja_recycler_basculas = findViewById(R.id.caja_recycler_basculas);

        regresar_otravez_formulario = findViewById(R.id.regresar_otravez_formulario);
        agregar_otra_bascula = findViewById(R.id.agregar_otra_bascula);




        ArrayAdapter<String> adaptador =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,BASCULAS);
        automarca.setAdapter(adaptador);
        automarca.setTokenizer(new MultiAutoCompleteTextView.Tokenizer() {
            public int findTokenStart(CharSequence text, int cursor) {
                int i = cursor;

                while (i > 0 && text.charAt(i - 1) != ' ') {
                    i--;
                }
                while (i < cursor && text.charAt(i) == ' ') {
                    i++;
                }

                return i;
            }

            public int findTokenEnd(CharSequence text, int cursor) {
                int i = cursor;
                int len = text.length();

                while (i < len) {
                    if (text.charAt(i) == ' ') {
                        return i;
                    } else {
                        i++;
                    }
                }

                return len;
            }

            public CharSequence terminateToken(CharSequence text) {
                int i = text.length();

                while (i > 0 && text.charAt(i - 1) == ' ') {
                    i--;
                }

                if (i > 0 && text.charAt(i - 1) == ' ') {
                    return text;
                } else {
                    if (text instanceof Spanned) {
                        SpannableString sp = new SpannableString(text + " ");
                        TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                                Object.class, sp, 0);
                        return sp;
                    } else {
                        return text + " ";
                    }
                }
            }
        });


        final int permisoLocacion = ContextCompat.checkSelfPermission(Mapa.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permisoLocacion != PackageManager.PERMISSION_GRANTED) {
            solicitarPermisoLocation();

            Log.e("paso", "paso");
        }

        convertidor = new CoordinateConverter();
        UTMPoint p = fromGeodeticToUTM(longitud, latitud);


        iniciar_verificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formulario_principal.setVisibility(View.VISIBLE);
                consultarFormaMedicamento();
                mapaid.setVisibility(View.GONE);
                caja_punto_partida.setVisibility(View.GONE);


            }
        });
        regresar_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                formulario_principal.setVisibility(view.GONE);
                mapaid.setVisibility(view.VISIBLE);
                caja_punto_partida.setVisibility(View.VISIBLE);
            }
        });


        guardar_nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nuevo_nombre = nombre_texto.getText().toString();
                nombre.setText(nuevo_nombre);
                if (!nuevo_nombre.trim().equals("")) {
                    caja_edit_nombre.setVisibility(View.GONE);
                    caja_nombre_final.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "El nombre es necesario.", Toast.LENGTH_LONG).show();
                }
            }
        });
        cambiar_nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                caja_nombre_final.setVisibility(view.GONE);
                caja_edit_nombre.setVisibility(view.VISIBLE);
            }
        });
        guardar_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cuenta = tel_texto.getText().toString().trim().length();
                if (cuenta == 10) {
                    tel10 = true;
                } else {
                    tel10 = false;
                }
                nuevo_tel = tel_texto.getText().toString();
                telefono.setText(nuevo_tel);
                if (!nuevo_tel.trim().equals("")) {
                    if (tel10 == true) {


                        caja_edit_tel.setVisibility(View.GONE);
                        caja_tel_final.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "El telefono debe ser de 10 digitos.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "El telefono es necesario.", Toast.LENGTH_LONG).show();
                }

            }
        });
        cambiar_telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                caja_tel_final.setVisibility(view.GONE);
                caja_edit_tel.setVisibility(view.VISIBLE);
            }
        });


        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date());
        Log.e("fecha", "" + date);
        fecha.setText(date);

        /*fecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean tieneFoco) {

                if(!tieneFoco)
                {
                    fecha_final_Str=fecha.getText().toString().trim().toLowerCase();
                    if (!fecha_final_Str.equals("")&&fecha_final_Str!=null)
                    {
                        // String regex = "^(.+)@(.+)$";

                        String regexUsuario = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
                        Pattern pattern = Pattern.compile(regexUsuario);
                        Matcher matcher = pattern.matcher(fecha_final_Str);
                        if(matcher.matches()==true){

                            correo_exitoso=true;

                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Ingrese correo valido.",Toast.LENGTH_LONG).show();

                }
            }
        });*/

        guardar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        adapterGiro = new AdapterGiro(activity, R.layout.lista_giro, listaGiro, getResources());
        giros.setAdapter(adapterGiro);

        adapterMercado = new AdapterMercado(activity, R.layout.lista_mercado, listaMercado, getResources());
        mercado.setAdapter(adapterMercado);

        adapterTipoInstrumento = new AdapterTipoInstrumento(activity, R.layout.lista_tipo_instrumento, listaInstrumen, getResources());
        tipoInstrumento.setAdapter(adapterTipoInstrumento);

        adapterMarcaBasculas = new AdapterMarcaBasculas(activity, R.layout.item, listaMarca, getResources());
        recycler_marca.setAdapter(adapterMarcaBasculas);








        adapterClaseExactitud = new AdapterClaseExactitud(activity, R.layout.lista_clase_exactitud, listaClase, getResources());
        ClaseExactitud.setAdapter(adapterClaseExactitud);

        /*adapterCantidadBasculas=new AdapterCantidadBasculas(activity,R.layout.item6,listaCantidadBasc,getResources());
        recycler_numero_basc.setAdapter(adapterCantidadBasculas);
*/
        guardar_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nueva_serie = serie_texto.getText().toString();
                numero_serie.setText(nueva_serie);
                if (!nueva_serie.trim().equals("")) {
                    caja_edit_serie.setVisibility(View.GONE);
                    caja_serie_final.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "El numero de serie es necesario.", Toast.LENGTH_LONG).show();
                }
            }
        });
        cambiar_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                caja_serie_final.setVisibility(view.GONE);
                caja_edit_serie.setVisibility(view.VISIBLE);
            }
        });
        guardar_costo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nuevo_costo = costo_texto.getText().toString();
                costo.setText("$"+nuevo_costo);
                if (!nuevo_costo.trim().equals("")) {
                    caja_edit_costo.setVisibility(View.GONE);
                    caja_costo_final.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "El costo es necesario.", Toast.LENGTH_LONG).show();
                }
            }
        });
        cambiar_costo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                caja_costo_final.setVisibility(view.GONE);
                caja_edit_costo.setVisibility(view.VISIBLE);
            }
        });
        regresar_formulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                formulario_principal.setVisibility(view.VISIBLE);
                formulario_bascula.setVisibility(view.GONE);

            }
        });
        finalizar_reg_bascula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                formulario_bascula.setVisibility(view.GONE);
                caja_finalizar_basc.setVisibility(view.VISIBLE);
            }
        });
        finalizar_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                caja_finalizar_basc.setVisibility(view.GONE);
                formulario_bascula.setVisibility(View.VISIBLE);
            }
        });
        finalizar_si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Intent cambio_form= new Intent(Mapa.this,ActaDictamen.class);
               startActivity(cambio_form);
            }
        });
        giros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               listas_giro = findViewById(R.id.listaGiro);
                if (listas_giro == null) {
                    listas_giro = (TextView) view.findViewById(R.id.listaGiro);
                } else {
                    listas_giro = (TextView) view.findViewById(R.id.listaGiro);
                }
                seleccion_giro = listas_giro.getText().toString();
                Log.e("tipogiro", "" + seleccion_giro);


                Log.e("tipo", "" + seleccion_giro);
                Log.e("tipo", "" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mercado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listas_mercado = findViewById(R.id.listaMercado);
                if (listas_mercado == null) {
                    listas_mercado = (TextView) view.findViewById(R.id.listaMercado);
                } else {
                    listas_mercado = (TextView) view.findViewById(R.id.listaMercado);
                }
                seleccion_mercado = listas_mercado.getText().toString();
                Log.e("tipogiro", "" + seleccion_mercado);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        valorCheckbox="";
        //inicial.setChecked(true);
        inicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitar_foco();
                inicial.setChecked(true);
                valorCheckbox="Inicial";
                Log.e("cambios", "" + valorCheckbox);
            }
        });
        anual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitar_foco();
                anual.setChecked(true);
                valorCheckbox="Anual";
                Log.e("cambios", "" + valorCheckbox);
            }
        });
        primerSemestre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitar_foco();
                primerSemestre.setChecked(true);
                valorCheckbox="Primer Semestre";
                Log.e("cambios", "" + valorCheckbox);
            }
        });
        segundoSemestre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitar_foco();
                segundoSemestre.setChecked(true);
                valorCheckbox="Segundo Semestre";
                Log.e("cambios", "" + valorCheckbox);
            }
        });
        extraordinaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitar_foco();
                extraordinaria.setChecked(true);
                valorCheckbox="Extraordinaria";
                Log.e("cambios", "" + valorCheckbox);
            }
        });

        siguiente_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cuenta = tel_texto.getText().toString().trim().length();
                if (cuenta == 10) {
                    tel10 = true;
                } else {
                    tel10 = false;
                }
                nuevo_nombre = nombre_texto.getText().toString();
                nuevo_tel = tel_texto.getText().toString();
                if (!nuevo_nombre.trim().equals("")) {
                    if(!seleccion_giro.trim().equals("Giro")){
                        if(!seleccion_mercado.trim().equals("Mercado")){
                            if (!nuevo_tel.trim().equals("")) {
                                if (tel10==true){
                                    formulario_principal.setVisibility(view.GONE);
                                    formulario_bascula.setVisibility(view.VISIBLE);
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "El telefono debe ser de 10 digitos.", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Seleccionar mercado es necesario.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Seleccionar giro es necesario.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "El nombre es necesario.", Toast.LENGTH_LONG).show();
                }
            }
        });
        tipoInstrumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listas_intrusmento = findViewById(R.id.listaInstrumento);
                if (listas_intrusmento == null) {
                    listas_intrusmento = (TextView) view.findViewById(R.id.listaInstrumento);
                } else {
                    listas_intrusmento = (TextView) view.findViewById(R.id.listaInstrumento);
                }
                seleccion_instrumento = listas_intrusmento.getText().toString();
                Log.e("tipoInstrumento", "" + seleccion_instrumento);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        guardar_marca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conexion3=new Conexion(getApplicationContext(),"basculas",null,1);
                database3=conexion3.getReadableDatabase();
                nueva_marca = automarca.getText().toString();
                marca_basc.setText(nueva_marca);
                try {
                    String[] parametros = {nueva_marca.trim()};
                    Log.e("nueva_marca",""+nueva_marca);
                    cursor3= database3.rawQuery("SELECT DISTINCT marca,modelo FROM basculas WHERE marca=?",parametros);
                    Log.e("marca",""+cursor3);

                    int cuenta =cursor3.getCount();
                    Log.e("modelo_cuenta",""+cuenta);
                    listaModelo.clear();
                    while (cursor3.moveToNext()){
                        Log.e("modelo",cursor3.getString(1));
                        listaModelo.add(new ModeloRecycler(cursor3.getString(1)));

                        //Log.e("lista_basculas",""+BASCULAS);
                    }
                    setListaModelo();
                    Log.e("lista_basculas",""+cursor2);
                }catch (Exception e){
                    Log.e("basculas","no existe");
                }
                if (!nueva_marca.trim().equals("")) {
                    caja_auto_marca.setVisibility(View.GONE);
                    caja_marca_final.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "La marca es necesaria.", Toast.LENGTH_LONG).show();
                }
            }
        });
        cambiar_marca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                caja_marca_final .setVisibility(view.GONE);
                caja_auto_marca.setVisibility(view.VISIBLE);
            }
        });

        ClaseExactitud.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 listas_exactitud = findViewById(R.id.listaClaseExactitud);
                if (listas_exactitud == null) {
                    listas_exactitud = (TextView) view.findViewById(R.id.listaClaseExactitud);
                } else {
                    listas_exactitud = (TextView) view.findViewById(R.id.listaClaseExactitud);
                }
                seleccion_exactitud = listas_exactitud.getText().toString();
                Log.e("tipoexactitud", "" + seleccion_exactitud);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        agregar_bascula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccion_instrumento = listas_intrusmento.getText().toString();
                nueva_marca = automarca.getText().toString();
                nueva_serie = serie_texto.getText().toString();
                checkModel=modeloSHER.getString("modelo","no");
                checkAlcanceMax=alcanceMaxSher.getString("alcanceMax","no");
                checkEod=eodSher.getString("alcanceEod","NO");
                checkAlcanceMin=alcanceMinSher.getString("alcanceMin","no");
                nuevo_costo = costo_texto.getText().toString();
                Log.e("bascula","listo");
                Log.e("cambiar_2",""+checkModel);
                Log.e("cambio3",""+selector_modelo);
                if (!seleccion_instrumento.trim().equals("Tipo de instrumento")) {
                    if(!nueva_marca.trim().equals("")){
                        if(!checkModel.trim().equals("")){
                            if(!nueva_serie.trim().equals("")){
                                if(!checkAlcanceMax.trim().equals("")){
                                    if(!checkEod.trim().equals("")){
                                        if(!checkAlcanceMin.trim().equals("")){
                                            if(!seleccion_exactitud.trim().equals("Clase de exactitud")){
                                                if(!valorCheckbox.trim().equals("")){
                                                    if(!nuevo_costo.trim().equals("")){
                                                        formulario_bascula.setVisibility(View.GONE);
                                                        almacen_basculas.setVisibility(View.VISIBLE);

                                                        JSONObject jsonObject=new JSONObject();
                                                        json_datos_bascula=new JSONArray();
                                                        try {
                                                            jsonObject.put("marca",nueva_marca);
                                                            jsonObject.put("tipo_intrsumento",seleccion_instrumento);
                                                            jsonObject.put("modelo",checkModel);
                                                            jsonObject.put("serie",nueva_serie);
                                                            jsonObject.put("Alcance_max",checkAlcanceMax);
                                                            jsonObject.put("eod",checkEod);
                                                            jsonObject.put("alcance_min",checkAlcanceMin);
                                                            jsonObject.put("exactitud",seleccion_exactitud);
                                                            jsonObject.put("checkbox",valorCheckbox);
                                                            jsonObject.put("costo",nuevo_costo);

                                                            json_datos_bascula.put(jsonObject);

                                                            Log.e("1", String.valueOf(jsonObject));
                                                            Log.e("2", String.valueOf(json_datos_bascula));
                                                            for (int i=0; i<json_datos_bascula.length();i++)
                                                            {
                                                                try { JSONObject jsonSacando= json_datos_bascula.getJSONObject(i);
                                                                   String strMarca=jsonSacando.get("marca").toString();
                                                                   String strTipo_intrsumento=jsonSacando.get("tipo_intrsumento").toString();
                                                                   String strModelo=jsonSacando.get("modelo").toString();
                                                                   String strSerie=jsonSacando.get("serie").toString();
                                                                   String strAlcance_max=jsonSacando.get("Alcance_max").toString();
                                                                   String strEod=jsonSacando.get("eod").toString();
                                                                   String strAlcance_min=jsonSacando.get("alcance_min").toString();
                                                                   String strExactitud=jsonSacando.get("exactitud").toString();
                                                                   String strCheckbox=jsonSacando.get("checkbox").toString();
                                                                   String strCosto=jsonSacando.get("costo").toString();


                                                                    //listaCantidadBasc.clear();

                                                                    listaCantidadBasc.add(new CantidadBasculasRecycler(strMarca,strTipo_intrsumento,strModelo,
                                                                            strSerie,strAlcance_max,strEod,strAlcance_min,strExactitud,strCheckbox,strCosto));
                                                                    adapterCantidadBasculas=new AdapterCantidadBasculas(activity,R.layout.item6,listaCantidadBasc,getResources());
                                                                    recycler_numero_basc.setAdapter(adapterCantidadBasculas);

                                                                    Log.e("t1",strMarca);
                                                                    Log.e("t2",strTipo_intrsumento);
                                                                    Log.e("t3",strModelo);
                                                                    Log.e("t4",strSerie);
                                                                    Log.e("t5",strAlcance_max);
                                                                    Log.e("t6",strEod);
                                                                    Log.e("t7",strAlcance_min);
                                                                    Log.e("t8",strExactitud);
                                                                    Log.e("t9",strCheckbox);
                                                                    Log.e("t10",strCosto);

                                                                    //Log.e("json"+i,jsonObject.getString(nueva_marca));
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }


                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getApplicationContext(), "Ingregso el costo.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                                else
                                                {
                                                    Toast.makeText(getApplicationContext(), "Seleccione tipo de visita.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(), "Seleccione la clase de exactitud.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "Seleccione el Alcance Minimo.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Seleccione el Eod.", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "Seleccione el alcance Max.", Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "El numero de serie es necesario.", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Seleccione  un modelo.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "La marca es necesaria.", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "La clase de instrumento es necesaria.", Toast.LENGTH_LONG).show();
                }
            }
        });
        regresar_otravez_formulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                almacen_basculas.setVisibility(View.GONE);
                formulario_principal.setVisibility(View.VISIBLE);
            }
        });
        agregar_otra_bascula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formulario_bascula.setVisibility(View.VISIBLE);
                automarca.setText("");
                caja_auto_marca.setVisibility(View.VISIBLE);
                caja_marca_final.setVisibility(View.GONE);
                tipoInstrumento.setSelection(0);
                recycler_modelo.setAdapter(adapterModeloBasculas);
                serie_texto.setText("");
                caja_serie_final.setVisibility(View.GONE);
                caja_edit_serie.setVisibility(View.VISIBLE);
                recycler_alcance.setText("");
                //recycler_eod.setAdapter(adapterEoD);
                recycler_minimo.setText("");
                ClaseExactitud.setSelection(0);
                valorCheckbox="";
                costo_texto.setText("");
                caja_costo_final.setVisibility(View.GONE);
                caja_edit_costo.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        LatLng punto1 = new LatLng(19.4234247, -99.1269502);
        LatLng punto2 = new LatLng(19.4240088, -99.1385345);
        LatLng punto3 = new LatLng(19.3543387, -99.0942125);
        LatLng punto4 = new LatLng(19.3498456, -99.0843095);
        LatLng punto5 = new LatLng(19.3506611, -99.0864875);
        LatLng punto6 = new LatLng(19.3411523, -99.103033);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(punto1).title("Mercado morelos"));
        mMap.addMarker(new MarkerOptions().position(punto2).title("Mercado sonora"));
        mMap.addMarker(new MarkerOptions().position(punto3).title("Mercado renovacion"));
        mMap.addMarker(new MarkerOptions().position(punto4).title("Mercado Margarita Maza de juarez"));
        mMap.addMarker(new MarkerOptions().position(punto5).title("Mercado topo"));
        mMap.addMarker(new MarkerOptions().position(punto6).title("Mercado HUEVO"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        miLatLong();


        Location punto_mercado = new Location("Bascula");
        punto_mercado.setLatitude(19.3506611);
        punto_mercado.setLongitude(-99.0864875);
        Location punto_usuario = new Location("Usuario");
        punto_usuario.setLatitude(latitud);
        punto_usuario.setLongitude(longitud);
        Log.e("mercado", "" + punto_mercado);
        Log.e("usuario", "" + punto_usuario);

        float distancias = punto_mercado.distanceTo(punto_usuario);
        float restriccion = 30;
        Log.e("distancia", "" + distancias);
        if (distancias < restriccion) {
            iniciar_verificacion.setVisibility(View.VISIBLE);

        } else {
            Log.e("distancia2", "" + distancias);
            Toast.makeText(getApplicationContext(), "Aun no llegas a tu destino.", Toast.LENGTH_LONG).show();

        }

    }

    private void solicitarPermisoLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISO_LOCATION);
        Log.e("va", "va");
    }

    public void checarPermisos() {
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Intent irPermisos = new Intent(Mapa.this, ActivarPermisos.class);
            startActivity(irPermisos);
        }
    }

    private void miLatLong(){

        /** SE PIDEN PERMISOS DE LOCACIÓN PARA*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /** MANAGER DE LOCACIONES DE ANDROID*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        actualizarUbicacion(location);


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Long.parseLong(String.valueOf(tiempo_actualizacion)), 0, locationListener);
    }



    private void actualizarUbicacion(Location location) {
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            altitud=location.getAltitude();
            agregarMarcadorUbicacion(latitud, longitud);
            direccion = darDireccion(this, latitud, longitud);
            UTMPoint p = fromGeodeticToUTM(longitud, latitud);
            LatLng coord = new LatLng(latitud,longitud);
            String[] direccion_fragmentada=direccion.split(",");
            for (int i=0;i<direccion_fragmentada.length;i++){
                Log.e("direccion_fragmentada",direccion_fragmentada[i]);
            }
            Log.e("lat",""+latitud);
            Log.e("lon",""+longitud);
            coordenadas =coord;
            direccion_mercado.setText(direccion);
             UTMPoint conver=fromGeodeticToUTM(punto);
             Log.e("con",""+conver);
             UTMPoint conver1=fromGeodeticToUTM(longitud,latitud);
             Log.e("con1",""+conver1);
             UTMPoint conver2=fromGeodeticToUTM(longitud,latitud,WGS84);
             Log.e("con2",""+conver2);

             String strConver= String.valueOf(conver2);
             Log.e("str",""+strConver);

             String[] X_Y_Z=strConver.split(" ");
             Log.e("corte",""+X_Y_Z[3].toString().replace(",","").replace("(",""));
             String x= String.valueOf(conver2.getX());
             String y=String.valueOf(conver2.getY());
             String z=String.valueOf(conver2.getZone());

            latitud_x.setText(X_Y_Z[0].toString().replace(",","").replace("(",""));
            longitud_y.setText(X_Y_Z[1].toString().replace(",","").replace("(",""));
             GeodeticPoint conver3=fromUTMToGeodetic(x,y,z);
            zona.setText(X_Y_Z[3].toString().replace(",","").replace("(","").replace(")","")+"N");

             Log.e("con3",""+conver3);






            //Toast.makeText(getApplicationContext(),"direccion: "+direccion,Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(),"lat: "+latitud+"long:"+longitud,Toast.LENGTH_LONG).show();
            Context context = this;
            SharedPreferences preferencias = getSharedPreferences("Usuario", context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("latitud", String.valueOf(latitud));
            editor.putString("longitud", String.valueOf(longitud));
            editor.putString("direccion", "" + direccion);
            editor.apply();
            if (latitud!=0){
                //segundoPlano = new SegundoPlano();
                //segundoPlano.execute();
            }
        }
    }
    public String darDireccion(Context ctx, double darLat, double darLong) {
        String fullDireccion = null;
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> direcciones = geocoder.getFromLocation(darLat, darLong, 1);
            if (direcciones.size() > 0) {
                Address direccion = direcciones.get(0);
                fullDireccion = direccion.getAddressLine(0);
                String ciudad = direccion.getLocality();
                String pueblo = direccion.getCountryName();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fullDireccion;
    }
    public Bitmap resizeBitmap(String drawableName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(drawableName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }
    private void agregarMarcadorUbicacion(double latitud, double longitud) {

        latLong = new LatLng(latitud, longitud);
        if (marker != null) {
            marker.remove();
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLong)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("bascula", 70, 70))));
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLong, 18);
            mMap.moveCamera(miUbicacion);
        }
        else{
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLong)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("bascula", 70, 70))));
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLong, 18);
            mMap.animateCamera(miUbicacion);
        }

        marker.setZIndex(0);
    }
    LocationListener locationListener = new LocationListener() {
        //Cuando la ubicación cambia
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
            Location locationA = new Location("Primera");
            latUpdate = location.getLatitude();
            longUpdate = location.getLongitude();
            locationA.setLatitude(latUpdate);
            locationA.setLongitude(longUpdate);
            //Location locationB = new Location("point B");
            //String latitudX = sharedPreferences.getString("latitud", "no");
            //String longitudX = sharedPreferences.getString("longitud", "no");
            /*if (!latitudX.equals("no") && !longitudX.equals("no")) {
                locationB.setLatitude(Double.parseDouble(latitudX));
                locationB.setLongitude(Double.parseDouble(longitudX));
                float distance = locationA.distanceTo(locationB);
                if (distance >= 3)//era 10 antes
                {
                    mMap.clear();
                    miLatLong();
                }
                //strLat = datosViaje.getString("lat"," ");
                //strLong = datosViaje.getString("lng"," ");
                //strEmpresa = datosViaje.getString("empresa"," ");
                //strEstado = datosViaje.getString("estado"," ");
                if (strEstado.equals("destino")){
                    Location locationC = new Location("point B");
                    locationC.setLatitude(Double.parseDouble(strLat));
                    locationC.setLongitude(Double.parseDouble(strLong));
                    float distanciaDestino = locationC.distanceTo(locationA);
                    Log.e("distancia",""+distanciaDestino);
                    if (distanciaDestino < 70)//distancia en metros a punto de partida
                    {
                        //Log.e("distancia","es menor a 1");
                        //if ()
                        capaDestino.setVisibility(View.GONE);
                        capaLlegada.setVisibility(View.VISIBLE);
                    }
                }
            }*/
        }
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISO_LOCATION : {
                Log.e("confirma","confirma");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Intent reiniciar=new Intent(Mapa.this,ActivarPermisos.class);
                    startActivity(reiniciar);
                } else {
                    Intent valcuacion=new Intent(Mapa.this,Mapa.class);
                    startActivity(valcuacion);

                    Log.e("AQUIMAMO","AQUIMAMO");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void consultarFormaMedicamento(){

        conexion2=new Conexion(getApplicationContext(),"basculas",null,1);
        database2=conexion2.getReadableDatabase();



        try {
            cursor1= database.rawQuery("SELECT alcance_maximo,eod,tipo,alcance_minimo,alcance_medicion,clase_exactitud,marco_pesas,pesa_5kg,pesa_10kg,pesa_20kg,pesa_clase_exactitud,horario FROM catalogo  ",null);
            // Log.e("tipos",""+cursor1);
            int cuenta =cursor1.getCount();
            Log.e("catalogo",""+cuenta);
            while (cursor1.moveToNext()){
                Log.e("*",cursor1.getString(0));
                Log.e("horario",cursor1.getString(1));
            }
            Log.e("horario",cuenta+" -- ");
            }catch (Exception e){
            Log.e("catalogo","no existe");
            }
        try {
             cursor2= database2.rawQuery("SELECT DISTINCT marca FROM basculas  ",null);
            // Log.e("tipos",""+cursor1);
            int cuenta =cursor2.getCount();
            Log.e("basculas",""+cuenta);
            while (cursor2.moveToNext()){
                Log.e("marca",cursor2.getString(0));
                BASCULAS.add(cursor2.getString(0));
                //Log.e("lista_basculas",""+BASCULAS);
            }
            Log.e("lista_basculas",""+BASCULAS);
        }catch (Exception e){
            Log.e("basculas","no existe");
        }
    }
    public void setListaGiro()
    {
        listaGiro.clear();
        String coy[] = {"", "Abarrotes","Cafetería",
                "Carnicería", "Casas de empeño","Chocolatería","Compra-venta de chatarra",
                "Compra-venta de papel y cartón", "Confitería","Cremería","Desperdicios Industriales",
                "Dulcería", "Ferretería","Huevo","Joyería",
                "Lavandería", "Materiales de Construcción", "Mercado sobre ruedas",
                "Mini súper", "Miscelánea","Molino","Panadería",
                "Pescados y mariscos", "Pollería","Productos del Campo","Recaudería",
                "Restaurant", "Rosticería","Semillas y chiles secos","Supermercados",
                "Taquería", "Tiendas Departamentales","Tlapalería", "Tortillería","Otros","Contenedores Marítimos",
                "Agropecuarias y/o Ganaderas", "Comerciales","De Servicios","Industriales",
                "Básculas Públicas", "Industria Automotriz"};
        for (int i=0; i<coy.length;i++)
        {
            final SpinnerModel sched = new SpinnerModel();
            sched.ponerNombre(coy[i]);
            //sched.ponerImagen("spinner"+i);
            sched.ponerImagen("spi_"+i);
            listaGiro.add(sched);
        }
    }
    public void setListaMercado()
    {
        listaMercado.clear();
        String coy[] = {"", "Mercado topo","M.D.C y columnas de concreto",
                "M.D.C y columnas de acero", "M.D.C y columnas mixtas"};
        for (int i=0; i<coy.length;i++)
        {
            final SpinnerModel sched = new SpinnerModel();
            sched.ponerNombre(coy[i]);
            //sched.ponerImagen("spinner"+i);
            sched.ponerImagen("spi_"+i);
            listaMercado.add(sched);
        }
    }
    public void setListaInstrumen()
    {

        listaInstrumen.clear();
        String coy[] = {"", "M=Mecanica","E=Electronica",
                "EM=Electromecanica"};

        for (int i=0; i<coy.length;i++)
        {
            final SpinnerModel sched = new SpinnerModel();
            sched.ponerNombre(coy[i]);
            //sched.ponerImagen("spinner"+i);
            //sched.ponerImagen("spi_"+i);
            listaInstrumen.add(sched);
 
        }
    }
    private void quitar_foco()
    {
        inicial.setChecked(false);
        primerSemestre.setChecked(false);
        segundoSemestre.setChecked(false);
        anual.setChecked(false);
        extraordinaria.setChecked(false);

    }
    public void setListaMarca()
    {
        listaMarca.clear();
        String coy[] = {"Microlife","Braunker",
                "Ohaus","Transcell","Sartorius","Adam equipemet","Sartorius",};
        for (int i=0; i<coy.length;i++)
        {

            listaMarca.add(new MarcaRecycler(coy[i]));
        }
    }

    public void setListaModelo()
    {
        adapterModeloBasculas = new AdapterModeloBasculas(activity, R.layout.item2, listaModelo, getResources());
        recycler_modelo.setAdapter(adapterModeloBasculas);
    }



    public void setListaClase()
    {
        listaClase.clear();
        String coy[] = {"", "Ordinaria (llll)","Media(lll)"};
        for (int i=0; i<coy.length;i++)
        {
            final SpinnerModel sched = new SpinnerModel();
            sched.ponerNombre(coy[i]);
            //sched.ponerImagen("spinner"+i);
            sched.ponerImagen("spi_"+i);
            listaClase.add(sched);
        }
    }
    public void setListaCantidadBasc()
    {

    }

    @Override
    public void onBackPressed() {

    }
    private void limpiarAcentos(String cadena){
        String limpia=cadena.replace("á","a");
        limpia=limpia.replace("é","e");
        limpia=limpia.replace("í","i");
        limpia=limpia.replace("ó","o");
        limpia=limpia.replace("ú","u");

        Log.e("busqueda",limpia);

        automarca.setText(limpia);
    }

    public void definirAlcance(String modelo){
        conexion1=new Conexion(getApplicationContext(),"catalogo",null,1);
        database=conexion1.getReadableDatabase();
        conexion4=new Conexion(getApplicationContext(),"basculas",null,1);
        database4=conexion4.getReadableDatabase();
        marca_basc.setText(nueva_marca);

        modelo.trim();
        Log.e("MODELO_MAPA",""+modelo);
        try {
            String[] parametros = {modelo.trim()};
            Log.e("parametros",""+modelo);
            cursor4= database4.rawQuery("SELECT alcance_maximo,alcance_minimo,modelo FROM basculas WHERE modelo=?",parametros);
            Log.e("alcance",""+cursor4);

            int cuenta =cursor4.getCount();
            Log.e("alcance_cuenta",""+cuenta);
            //listaAlcance.clear();
            while (cursor4.moveToNext()){
                Log.e("alcance",cursor4.getString(0));
                recycler_alcance.setText(cursor4.getString(0));
                recycler_minimo.setText(cursor4.getString(1));
                try {
                    String[] parametros2 = {cursor4.getString(0),cursor4.getString(1)};
                    cursor1= database.rawQuery("SELECT alcance_maximo,alcance_minimo,eod FROM catalogo WHERE alcance_maximo=? AND alcance_minimo=?",parametros2);
                     //Log.e("tipos",""+parametros2);
                    int cuenta2 =cursor1.getCount();
                    Log.e("catalogo",""+cuenta2);
                    while (cursor1.moveToNext()){
                        Log.e("1",cursor1.getString(0));
                        Log.e("eod",cursor1.getString(1));
                        recycler_eod.setText(cursor1.getString(2));
                    }
                    Log.e("eod",cuenta2+" -- ");
                }catch (Exception e){
                    Log.e("catalogo","no existe");
                }
            }

            Log.e("LISTA_aLCANCE",""+cursor4);
        }catch (Exception e){
            Log.e("ALCANCE_MAX","no existe");
        }

    }


}