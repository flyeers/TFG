package es.ucm.fdi.boxit.presentacion;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import es.ucm.fdi.boxit.R;

public class PDFView {

    public static void abrirVisorPDF(Context context, String pdfUrl) {
        try {

            Uri uri = Uri.parse(pdfUrl);


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {

            Toast.makeText(context, R.string.noPDFViewer, Toast.LENGTH_SHORT).show();
        }
    }
}
