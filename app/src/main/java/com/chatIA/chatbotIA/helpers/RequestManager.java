package com.chatIA.chatbotIA.helpers;

import android.content.Context;

import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.assistants.models.request.AssistantRequest;
import com.chatIA.chatbotIA.assistants.models.request.MessageRequest;
import com.chatIA.chatbotIA.assistants.models.request.RunRequest;
import com.chatIA.chatbotIA.assistants.models.request.ThreadRequest;
import com.chatIA.chatbotIA.assistants.models.response.AssistantResponse;
import com.chatIA.chatbotIA.assistants.models.response.ListAssistantResponse;
import com.chatIA.chatbotIA.assistants.models.response.ListMessageResponse;
import com.chatIA.chatbotIA.assistants.models.response.MessageResponse;
import com.chatIA.chatbotIA.assistants.models.response.RunResponse;
import com.chatIA.chatbotIA.assistants.models.response.RunStatusResponse;
import com.chatIA.chatbotIA.assistants.models.response.ThreadResponse;
import com.chatIA.chatbotIA.listener.IAssistantResponse;
import com.chatIA.chatbotIA.listener.IHttpRequest;
import com.chatIA.chatbotIA.listener.IImageResponse;
import com.chatIA.chatbotIA.listener.IListAssistantResponse;
import com.chatIA.chatbotIA.listener.IListMessageResponse;
import com.chatIA.chatbotIA.listener.IMessageResponse;
import com.chatIA.chatbotIA.listener.IResponseBody;
import com.chatIA.chatbotIA.listener.IRunResponse;
import com.chatIA.chatbotIA.listener.IRunStatusResponse;
import com.chatIA.chatbotIA.listener.ITextResponse;
import com.chatIA.chatbotIA.listener.IThreadResponse;
import com.chatIA.chatbotIA.models.request.images.ImageRequest;
import com.chatIA.chatbotIA.models.response.images.ImageResponse;
import com.chatIA.chatbotIA.models.speech.SpeechRequest;
import com.chatIA.chatbotIA.models.text.TextRequest;
import com.chatIA.chatbotIA.models.text.TextResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Administrador de solicitudes para interactuar con la API.
 */
public class RequestManager {

    private Context context;
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private Retrofit elevenLabs = new Retrofit.Builder()
            .baseUrl("https://api.elevenlabs.io/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    /**
     * Constructor para RequestManager.
     *
     * @param context el contexto en el cual el administrador de solicitudes está operando
     */
    public RequestManager(Context context) {
        this.context = context;
    }

    /**
     * Genera una imagen utilizando una solicitud de imagen.
     *
     * @param imageRequest  la solicitud de imagen
     * @param imageResponse el callback para manejar la respuesta de la imagen
     */
    public void generateImage(ImageRequest imageRequest, final IImageResponse imageResponse) {
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Call<ImageResponse> call = request.generateImage(context.getString(R.string.api_key), imageRequest);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if (!response.isSuccessful()) {
                    imageResponse.didError(response.message());
                    return;
                }
                imageResponse.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable throwable) {
                imageResponse.didError(throwable.getMessage());
            }
        });
    }

    // Asistentes

    /**
     * Crea un asistente utilizando una solicitud de asistente.
     *
     * @param beta              la versión beta de la API
     * @param assistantRequest  la solicitud de asistente
     * @param assistantResponse el callback para manejar la respuesta del asistente
     */
    public void createAssistant(String beta, AssistantRequest assistantRequest, final IAssistantResponse assistantResponse) {
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Call<AssistantResponse> call = request.createAssistant(context.getString(R.string.api_key),
                beta, assistantRequest);
        call.enqueue(new Callback<AssistantResponse>() {
            @Override
            public void onResponse(Call<AssistantResponse> call, Response<AssistantResponse> response) {
                if (!response.isSuccessful()) {
                    assistantResponse.didError(response.message());
                    return;
                }
                assistantResponse.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<AssistantResponse> call, Throwable throwable) {
                assistantResponse.didError(throwable.getMessage());
            }
        });
    }

    /**
     * Obtiene una lista de asistentes.
     *
     * @param beta                   la versión beta de la API
     * @param iListAssistantResponse el callback para manejar la respuesta de la lista de asistentes
     */
    public void getListAssistant(String beta, IListAssistantResponse iListAssistantResponse) {
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Call<ListAssistantResponse> call = request.getListAssistant(context.getString(R.string.api_key), beta);
        call.enqueue(new Callback<ListAssistantResponse>() {
            @Override
            public void onResponse(Call<ListAssistantResponse> call, Response<ListAssistantResponse> response) {
                if (!response.isSuccessful()) {
                    iListAssistantResponse.didError(response.message());
                    return;
                }
                iListAssistantResponse.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<ListAssistantResponse> call, Throwable throwable) {
                iListAssistantResponse.didError(throwable.getMessage());
            }
        });
    }

    /**
     * Crea un hilo utilizando una solicitud de hilo.
     *
     * @param beta            la versión beta de la API
     * @param threadRequest   la solicitud de hilo
     * @param iThreadResponse el callback para manejar la respuesta del hilo
     */
    public void createThread(String beta, ThreadRequest threadRequest, final IThreadResponse iThreadResponse) {
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Call<ThreadResponse> call = request.createThread(context.getString(R.string.api_key),
                beta, threadRequest);
        call.enqueue(new Callback<ThreadResponse>() {
            @Override
            public void onResponse(Call<ThreadResponse> call, Response<ThreadResponse> response) {
                if (!response.isSuccessful()) {
                    iThreadResponse.didError(response.message());
                    return;
                }
                iThreadResponse.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<ThreadResponse> call, Throwable throwable) {
                iThreadResponse.didError(throwable.getMessage());
            }
        });
    }

    /**
     * Crea un mensaje utilizando una solicitud de mensaje.
     *
     * @param beta             la versión beta de la API
     * @param threadId         el ID del hilo
     * @param message          la solicitud de mensaje
     * @param iMessageResponse el callback para manejar la respuesta del mensaje
     */
    public void createMessage(String beta, String threadId, MessageRequest message,
                              final IMessageResponse iMessageResponse) {
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Call<MessageResponse> call = request.createMessage(context.getString(R.string.api_key),
                beta, threadId, message);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!response.isSuccessful()) {
                    iMessageResponse.didError(response.message());
                    return;
                }
                iMessageResponse.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable throwable) {
                iMessageResponse.didError(throwable.getMessage());
            }
        });
    }

    /**
     * Crea una ejecución utilizando una solicitud de ejecución.
     *
     * @param beta         la versión beta de la API
     * @param threadId     el ID del hilo
     * @param runRequest   la solicitud de ejecución
     * @param iRunResponse el callback para manejar la respuesta de la ejecución
     */
    public void createRun(String beta, String threadId, RunRequest runRequest, final IRunResponse iRunResponse) {
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Call<RunResponse> call = request.createRun(context.getString(R.string.api_key),
                beta, threadId, runRequest);
        call.enqueue(new Callback<RunResponse>() {
            @Override
            public void onResponse(Call<RunResponse> call, Response<RunResponse> response) {
                if (!response.isSuccessful()) {
                    iRunResponse.didError(response.message());
                    return;
                }
                iRunResponse.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RunResponse> call, Throwable throwable) {
                iRunResponse.didError(throwable.getMessage());
            }
        });
    }

    // TODO: El estado no está disponible

    /**
     * Obtiene el estado de una ejecución.
     *
     * @param beta               la versión beta de la API
     * @param threadId           el ID del hilo
     * @param runId              el ID de la ejecución
     * @param iRunStatusResponse el callback para manejar la respuesta del estado de la ejecución
     */
    public void getRunStatus(String beta, String threadId, String runId,
                             final IRunStatusResponse iRunStatusResponse) {
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Call<RunStatusResponse> call = request.getRunStatus(context.getString(R.string.api_key),
                beta, threadId, runId);
        call.enqueue(new Callback<RunStatusResponse>() {
            @Override
            public void onResponse(Call<RunStatusResponse> call, Response<RunStatusResponse> response) {
                if (!response.isSuccessful()) {
                    iRunStatusResponse.didError(response.message());
                    return;
                }
                iRunStatusResponse.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RunStatusResponse> call, Throwable throwable) {
                iRunStatusResponse.didError(throwable.getMessage());
            }
        });
    }

    /**
     * Obtiene una lista de mensajes.
     *
     * @param beta                 la versión beta de la API
     * @param threadId             el ID del hilo
     * @param iListMessageResponse el callback para manejar la respuesta de la lista de mensajes
     */
    public void getListMessage(String beta, String threadId, final IListMessageResponse iListMessageResponse) {
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Call<ListMessageResponse> call = request.getListMessage(context.getString(R.string.api_key),
                beta, threadId);
        call.enqueue(new Callback<ListMessageResponse>() {
            @Override
            public void onResponse(Call<ListMessageResponse> call, Response<ListMessageResponse> response) {
                if (!response.isSuccessful()) {
                    iListMessageResponse.didError(response.message());
                    return;
                }
                iListMessageResponse.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<ListMessageResponse> call, Throwable throwable) {
                iListMessageResponse.didError(throwable.getMessage());
            }
        });
    }

    public void sendMessage(TextRequest textRequest, final ITextResponse iChatResponse) {
        IHttpRequest request = retrofit.create(IHttpRequest.class);
        Call<TextResponse> call = request.generateMessage(context.getString(R.string.api_key), textRequest);
        call.enqueue(new Callback<TextResponse>() {
            @Override
            public void onResponse(Call<TextResponse> call, Response<TextResponse> response) {
                if (!response.isSuccessful()) {
                    iChatResponse.didError(response.message());
                    return;
                }
                iChatResponse.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<TextResponse> call, Throwable throwable) {
                iChatResponse.didError(throwable.getMessage());
            }
        });
    }

    public void generateSpeech(SpeechRequest speechRequest, final IResponseBody iResponseBody) {
        IHttpRequest request = elevenLabs.create(IHttpRequest.class);
        Call<ResponseBody> call = request.generateSpeech(speechRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    iResponseBody.didError(response.message());
                    return;
                }
                iResponseBody.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                iResponseBody.didError("Error generateSpeech(): " + throwable.getMessage());
            }
        });
    }
}
