package game.programming.whileloop.canvas_game;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by tuanamith on 5/27/18.
 */

public class VCP {
    public long question;
    public long vcp;

    DatabaseReference databaseReference;

    public VCP(long a, long b) {
        question = a;
        vcp = b;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public VCP() {}

}
