package com.example.slagalica.HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.Activities.Games.GameInterface;
import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.Activities.MainMenu.Settings;
import com.example.slagalica.Activities.MainMenu.SinglePlayerActivity;
import com.example.slagalica.MultiPlayer.Player;
import com.example.slagalica.R;

import pl.droidsonroids.gif.GifImageView;

public class DialogBuilder {



    // Dialog that shows if there is no set username
    public static androidx.appcompat.app.AlertDialog.Builder createAlertDialog(final Context context) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(context, Settings.class);
                        context.startActivity(intent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        androidx.appcompat.app.AlertDialog.Builder getActivityBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        getActivityBuilder
                .setMessage(context.getResources().getString(R.string.usernameMissing))
                .setPositiveButton((context.getResources().getString(R.string.yes)), dialogClickListener)
                .setNegativeButton(context.getResources().getString(R.string.no), dialogClickListener);
        return getActivityBuilder;
    }

    public static AlertDialog.Builder createAlertDialogForNewGame(final Context context) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, SinglePlayerActivity.class);
                SinglePlayerActivity.typeOfGame = TypeOfGame.SinglePlayer;
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SharedPreferences.Editor editorGames = context.getSharedPreferences(MainActivity.historyGamesPreferencesKey[TypeOfGame.SinglePlayer.getValue()], Context.MODE_PRIVATE).edit();
                        editorGames.clear();
                        editorGames.apply();
                        SharedPreferences.Editor editorPoints = context.getSharedPreferences(MainActivity.historyPointsPreferencesKey[TypeOfGame.SinglePlayer.getValue()], Context.MODE_PRIVATE).edit();
                        editorPoints.clear();
                        editorPoints.apply();
                        intent.putExtra(MainActivity.existGameInHistory, false);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        intent.putExtra(MainActivity.existGameInHistory, true);
                        break;
                }
                MainActivity.typeOfGame = 1;
                context.startActivity(intent);
            }
        };
        AlertDialog.Builder getSinglePlayerActivityBuilder = new AlertDialog.Builder(context);
        getSinglePlayerActivityBuilder
                .setMessage(context.getResources().getString(R.string.gameInHistory))
                .setPositiveButton(context.getResources().getString(R.string.newGame), dialogClickListener)
                .setNegativeButton(R.string.continueGame, dialogClickListener);
        return getSinglePlayerActivityBuilder;
    }



   public static AlertDialog.Builder challengeFromPlayer(final Player player, final Context context) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ConnectionController.getInstance().acceptChallengeFrom(context);
                        ConnectionController.getInstance().setListenerForGame(ConnectionController.getInstance().createGameListener(context,player));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        ConnectionController.getInstance().rejectChallengeFrom(context);
                        break;
                }
            }
        };
        androidx.appcompat.app.AlertDialog.Builder getActivityBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        getActivityBuilder
                .setMessage(context.getResources().getString(R.string.challenged) + " " + player.getUsername())
                .setPositiveButton((context.getResources().getString(R.string.yes)), dialogClickListener)
                .setCancelable(false)
                .setNegativeButton(context.getResources().getString(R.string.no), dialogClickListener);
        return getActivityBuilder;
    }

    public static androidx.appcompat.app.AlertDialog.Builder createWaitingAlert(final String username, final Context mContext) {
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder1.setTitle(mContext.getResources().getString(R.string.waitingForPlayer) + " " + username);
        builder1.setCancelable(false);
        LinearLayout layout = new LinearLayout(mContext);
        layout.setLayoutParams(new LinearLayout.LayoutParams(100,100));
        layout.setGravity(Gravity.CENTER);
        final GifImageView gifImageView = new GifImageView(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                100,100,0.0f);
        gifImageView.setBackgroundResource(R.drawable.slagalicarotate);
        gifImageView.setLayoutParams(lp);
        layout.addView(gifImageView);
        builder1.setView(layout);
        builder1.setNeutralButton(mContext.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConnectionController.getInstance().refuseChallenge(mContext,MainActivity.player,username);
                        dialog.cancel();
                    }
                });

        return builder1;
    }


    public static androidx.appcompat.app.AlertDialog.Builder createAlertDialogForExit(final Context context)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(context,MainActivity.class);
                        context.startActivity(intent);
                        SinglePlayerActivity.game = null;
                        ((SinglePlayerActivity)context).finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        androidx.appcompat.app.AlertDialog.Builder getSinglePlayerActivityBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        getSinglePlayerActivityBuilder
                .setMessage(context.getResources().getString(R.string.exitFromSingleGame))
                .setPositiveButton(context.getResources().getString(R.string.yes),dialogClickListener)
                .setNegativeButton(context.getResources().getString(R.string.no), dialogClickListener);
        return getSinglePlayerActivityBuilder;
    }

    public static androidx.appcompat.app.AlertDialog.Builder createAlertDialogForExit(final GameInterface game, final CountDownTimer timer)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        game.endGame(true);
                        timer.cancel();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        androidx.appcompat.app.AlertDialog.Builder getSinglePlayerActivityBuilder = new androidx.appcompat.app.AlertDialog.Builder((Activity) game);
        getSinglePlayerActivityBuilder
                .setMessage(((Activity) game).getResources().getString(R.string.exitFromSingleGame))
                .setPositiveButton(((Activity) game).getResources().getString(R.string.yes),dialogClickListener)
                .setNegativeButton(((Activity) game).getResources().getString(R.string.no), dialogClickListener);
        return getSinglePlayerActivityBuilder;
    }

    public static android.app.AlertDialog.Builder createDialogForGame(String message, final Activity context) {
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
        builder1.setTitle(context.getResources().getString(R.string.endGame));
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.finish();
                        dialog.cancel();
                    }
                });

        return builder1;
    }

    public static android.app.AlertDialog.Builder dialogPlayerRefused(final Context context) {
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
        builder1.setTitle(context.getResources().getString(R.string.refusedGame));
        builder1.setMessage(context.getResources().getString(R.string.userRefusedGame));
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder1;
    }

    public static androidx.appcompat.app.AlertDialog.Builder exitGame(final Activity activity)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                        System.exit(1);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        androidx.appcompat.app.AlertDialog.Builder getSinglePlayerActivityBuilder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        getSinglePlayerActivityBuilder
                .setTitle(activity.getResources().getString(R.string.exitGame))
                .setMessage(activity.getResources().getString(R.string.exitFromSingleGame))
                .setPositiveButton(activity.getResources().getString(R.string.yes),dialogClickListener)
                .setNegativeButton(activity.getResources().getString(R.string.no), dialogClickListener);
        return getSinglePlayerActivityBuilder;
    }
}
