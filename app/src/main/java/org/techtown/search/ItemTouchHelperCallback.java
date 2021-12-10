package org.techtown.search;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

enum ButtonsState{ GONE, LEFT_VISIBLE, RIGHT_VISIBLE }
 //enum : 서로 관련있는 상수들을 모아서 심볼릭한 명칭으로 집합하는 것.

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperListener listener;
    private boolean swipeBack = false;
    private ButtonsState buttonsShowedState = ButtonsState.GONE;
    private static final float buttonWidth = 115;
    private RectF buttonInstance = null;
    private RecyclerView.ViewHolder currenrtItemViewHolder = null;
    public ItemTouchHelperCallback(ItemTouchHelperListener listener) {
        this.listener = listener;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int drag_flags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        int swipe_flags = ItemTouchHelper.START|ItemTouchHelper.END;

        return makeMovementFlags(drag_flags,swipe_flags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

        return listener.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());

    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onItemSwipe(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){ //스와이프 됫는지
            if(buttonsShowedState != ButtonsState.GONE){ //스와이프가 중간에 있는지 확인
                if(buttonsShowedState == ButtonsState.LEFT_VISIBLE){
                    dX = Math.max(dX, buttonWidth); //중간이 아니라 왼쪽으로 스와이프 되어있으면
                    Log.d("2222", "클릭함 2");
                }
                    //Math.max : 두
                if(buttonsShowedState == ButtonsState.RIGHT_VISIBLE) {
                    dX = Math.min(dX, -buttonWidth);
                    Log.d("2222", "클릭함 3");
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }else{
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            if(buttonsShowedState == ButtonsState.GONE){
                Log.d("2222", "클릭함 1");
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        currenrtItemViewHolder = viewHolder; //버튼을 그려주는 함수

        drawButtons(c, currenrtItemViewHolder);

    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder){
        float buttonWidthWithOutPadding = buttonWidth - 10;
        float corners = 5;
        View itemView = viewHolder.itemView;
        Paint p = new Paint();
        buttonInstance = null;

        if (buttonsShowedState == ButtonsState.LEFT_VISIBLE){   //오른쪽으로 스와이프 했을때 (왼쪽에 버튼이 보여지게 될 경우)
            RectF leftButton = new RectF(itemView.getLeft() + 10, itemView.getTop() + 10,
                    itemView.getLeft() + buttonWidthWithOutPadding, itemView.getBottom() - 10);
            p.setColor(Color.BLUE);
            c.drawRoundRect(leftButton, corners, corners, p);
            drawText(MainActivity.context.getString(R.string.수정), c, leftButton, p);
            buttonInstance = leftButton;

        } else if(buttonsShowedState == ButtonsState.RIGHT_VISIBLE){  //왼쪽으로 스와이프 했을때 (오른쪽에 버튼이 보여지게 될 경우)
            RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithOutPadding,
                    itemView.getTop() + 10, itemView.getRight() -10, itemView.getBottom() - 10);
            p.setColor(Color.RED);
            c.drawRoundRect(rightButton, corners, corners, p);
            drawText(MainActivity.context.getString(R.string.삭제), c, rightButton, p);
            buttonInstance = rightButton;
        }
    }

    //버튼의 텍스트 그려주기
    private void drawText(String text, Canvas c, RectF button, Paint p){
        float textSize = 25;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);
        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX() - (textWidth/2), button.centerY() + (textSize/2), p);
    }

    @Override public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if(swipeBack){
            swipeBack = false;
            return 0;
        }

        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                    if(swipeBack){
                    if(dX < -buttonWidth) buttonsShowedState = ButtonsState.RIGHT_VISIBLE;
                    else if(dX > buttonWidth) buttonsShowedState = ButtonsState.LEFT_VISIBLE;
                    if(buttonsShowedState != ButtonsState.GONE){
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c, @NotNull final RecyclerView recyclerView ,
                                      final RecyclerView.ViewHolder viewHolder, final float dX, final float dY ,
                                      final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView , final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY , final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ItemTouchHelperCallback.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                setItemsClickable(recyclerView, true);
                swipeBack = false;

                if(listener != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())){
                    if(buttonsShowedState == ButtonsState.LEFT_VISIBLE){
                        listener.onLeftClick(viewHolder.getAdapterPosition(), viewHolder);
                    }else if(buttonsShowedState == ButtonsState.RIGHT_VISIBLE){
                        listener.onRightClick(viewHolder.getAdapterPosition(), viewHolder);
                    }
                }
                buttonsShowedState = ButtonsState.GONE;
                currenrtItemViewHolder = null; return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable){
        for(int i = 0; i < recyclerView.getChildCount(); i++){
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

}
