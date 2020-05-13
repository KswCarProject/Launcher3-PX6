package android.support.v4.os;

import android.os.Parcelable;
import android.support.annotation.RequiresApi;

@RequiresApi(13)
/* compiled from: ParcelableCompatHoneycombMR2 */
class ParcelableCompatCreatorHoneycombMR2Stub {
    ParcelableCompatCreatorHoneycombMR2Stub() {
    }

    static <T> Parcelable.Creator<T> instantiate(ParcelableCompatCreatorCallbacks<T> callbacks) {
        return new ParcelableCompatCreatorHoneycombMR2(callbacks);
    }
}
