package com.chatIA.chatbotIA.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.helpers.LanguageHelper;
import com.chatIA.chatbotIA.listener.IThemeHandler;
import com.chatIA.chatbotIA.models.OptionItem;
import com.chatIA.chatbotIA.screens.EditProfileScreen;
import com.chatIA.chatbotIA.screens.LoginScreen;

import java.util.List;

/**
 * Adaptador para mostrar una lista de opciones de perfil en un RecyclerView.
 */
public class ProfileOptionAdapter extends RecyclerView.Adapter<ProfileOptionViewHolder> {
    private Context context;
    private List<OptionItem> options;
    private IThemeHandler themeHandler;
    private Intent intent;

    /**
     * Constructor para ProfileOptionAdapter.
     *
     * @param context el contexto en el cual el adaptador está operando
     * @param options la lista de opciones a mostrar
     * @param themeHandler el manejador de temas para manejar la selección de temas
     */
    public ProfileOptionAdapter(Context context, List<OptionItem> options, IThemeHandler themeHandler) {
        this.context = context;
        this.options = options;
        this.themeHandler = themeHandler;
    }

    /**
     * Llamado cuando RecyclerView necesita un nuevo ViewHolder del tipo dado para representar un elemento.
     *
     * @param parent el ViewGroup en el que la nueva Vista será añadida después de ser enlazada a
     *               una posición del adaptador
     * @param viewType el tipo de vista del nuevo View
     * @return un nuevo ProfileOptionViewHolder que contiene una Vista del tipo dado
     */
    @NonNull
    @Override
    public ProfileOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileOptionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_profile,
                parent, false));
    }

    /**
     * Llamado por RecyclerView para mostrar los datos en la posición especificada.
     *
     * @param holder el ViewHolder que debe ser actualizado para representar los contenidos del
     *               elemento en la posición dada en el conjunto de datos
     * @param position la posición del elemento dentro del conjunto de datos del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull ProfileOptionViewHolder holder, int position) {
        holder.getTvOption().setText(options.get(position).getName());
        holder.getIvArrow().setImageResource(options.get(position).getImage());

        holder.itemView.setOnClickListener(v -> {
            switch (position) {
                case 0:
                    intent = new Intent(context, EditProfileScreen.class);
                    context.startActivity(intent);
                    break;
                case 1:
                    showSizeSelectionDialog(holder);
                    break;
                case 2:
                    themeHandler.chooseTheme();
                    break;
                case 3:
                    Toast.makeText(context, "Todavía no funciona :(", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    chooseLanguage();
                    break;
                case 5:
                    Toast.makeText(context, "Todavía no funciona :(((", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(context, "Todavía no funciona :(((", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    signOut();
                    break;
            }
        });
    }

    private void chooseLanguage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.select_language))
                .setItems(new CharSequence[]{
                        context.getString(R.string.language_english),
                        context.getString(R.string.language_spanish)
                }, (dialog, which) -> {
                    switch (which) {
                        case 0: // English
                            LanguageHelper.changeLanguage(context, "en");
                            break;
                        case 1: // Spanish
                            LanguageHelper.changeLanguage(context, "es");
                            break;
                    }
                    // Reinicia la actividad actual para aplicar el nuevo idioma
                    Intent intent = ((Activity) context).getIntent();
                    ((Activity) context).finish();
                    context.startActivity(intent);
                    dialog.dismiss();
                });
        builder.create().show();
    }


    /**
     * Muestra un cuadro de diálogo de selección de tamaño.
     *
     * @param holder el ViewHolder de la opción seleccionada
     */
    private void showSizeSelectionDialog(ProfileOptionViewHolder holder) {
        // Crea una nueva instancia de AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Establece el título del cuadro de diálogo
        builder.setTitle("Seleccionar tamaño");

        // Crea un array de strings para las opciones de tamaño
        String[] sizeOptions = {"Pequeño", "Mediano", "Grande"};

        // Establece elementos de elección única para el cuadro de diálogo
        builder.setItems(sizeOptions, (dialog, which) -> {
            // Almacena el tamaño seleccionado en una variable (por ejemplo, String selectedSize)
            String selectedSize = sizeOptions[which];

            // Actualiza la interfaz de usuario o realiza acciones basadas en el tamaño seleccionado
            Toast.makeText(context, "Tamaño seleccionado: " + selectedSize, Toast.LENGTH_SHORT).show();

            // (Opcional) Establece el tamaño seleccionado como texto para el segundo elemento de la lista
            // holder.getTvOption().setText("Tamaño: " + selectedSize);
        });

        // Crea el cuadro de diálogo y lo muestra
        builder.create().show();
    }

    /**
     * Cierra sesión en Firebase, Facebook y Google, y redirige a la pantalla de inicio de sesión.
     */
    public void signOut() {
        // Cierra sesión en Firebase
        FirebaseAuth.getInstance().signOut();

        // Cierra sesión en Facebook
        LoginManager.getInstance().logOut();

        // Cierra sesión en Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        mGoogleSignInClient.signOut();

        Intent intent = new Intent(context, LoginScreen.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    /**
     * Retorna el número total de elementos en el conjunto de datos que posee el adaptador.
     *
     * @return el número total de elementos en este adaptador
     */
    @Override
    public int getItemCount() {
        return options.size();
    }
}

/**
 * ViewHolder para mostrar una opción de perfil en el ProfileOptionAdapter.
 */
class ProfileOptionViewHolder extends RecyclerView.ViewHolder {

    private TextView tvOption;
    private ImageView ivArrow;

    /**
     * Constructor para ProfileOptionViewHolder.
     *
     * @param itemView la vista del elemento
     */
    public ProfileOptionViewHolder(@NonNull View itemView) {
        super(itemView);
        tvOption = itemView.findViewById(R.id.tv_option);
        ivArrow = itemView.findViewById(R.id.iv_arrow);
    }

    /**
     * Obtiene el TextView de la opción.
     *
     * @return el TextView de la opción
     */
    public TextView getTvOption() {
        return tvOption;
    }

    /**
     * Establece el TextView de la opción.
     *
     * @param tvOption el nuevo TextView de la opción
     */
    public void setTvOption(TextView tvOption) {
        this.tvOption = tvOption;
    }

    /**
     * Obtiene el ImageView de la flecha.
     *
     * @return el ImageView de la flecha
     */
    public ImageView getIvArrow() {
        return ivArrow;
    }

    /**
     * Establece el ImageView de la flecha.
     *
     * @param ivArrow el nuevo ImageView de la flecha
     */
    public void setIvArrow(ImageView ivArrow) {
        this.ivArrow = ivArrow;
    }
}
