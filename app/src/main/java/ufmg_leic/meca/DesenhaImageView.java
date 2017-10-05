package ufmg_leic.meca;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;


public class DesenhaImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint currentPaint,
                  markPaint;

    public boolean drawRect = false;

    GlobalMapeamento Global = GlobalMapeamento.getInstance();

    public DesenhaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(Color.BLUE);  // alpha.r.g.b
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(7);

        markPaint = new Paint();
        markPaint.setDither(true);
        markPaint.setColor( Color.argb( 90, 25, 146, 171 ) );  // alpha.r.g.b
        markPaint.setStyle(Paint.Style.FILL);
        markPaint.setStrokeJoin(Paint.Join.ROUND);
        markPaint.setStrokeCap(Paint.Cap.ROUND);
        markPaint.setStrokeWidth(7);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int PassoX =(366)/Global.X;

        for (int i = 0; i <= Global.X; i++){
            canvas.drawLine(3 + PassoX*i, 3, 3 + PassoX*i, 610, currentPaint);
        }

        int PassoY = ( 607 )/Global.Y;

        for (int j = 0; j <= Global.Y; j++){
            canvas.drawLine( 3, 3 + PassoY*j, 369, 3 + PassoY*j, currentPaint);
        }


        for ( int k = 0; k < Global.Y; k++ ){
            for ( int l = 0; l < Global.X; l++ ){

                if ( Global.Mapa[k][l] == 1 ){

                    canvas.drawRect( 3 + PassoX*l, 3 + PassoY*k, 3 + PassoX*l + PassoX,  3 + PassoY*k + PassoY, markPaint );
                }
            }
        }
    }
}
