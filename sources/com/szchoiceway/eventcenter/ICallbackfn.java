package com.szchoiceway.eventcenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ICallbackfn extends IInterface {
    void notifyEvt(int i, int i2, int i3, byte[] bArr, String str) throws RemoteException;

    public static abstract class Stub extends Binder implements ICallbackfn {
        private static final String DESCRIPTOR = "com.szchoiceway.eventcenter.ICallbackfn";
        static final int TRANSACTION_notifyEvt = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICallbackfn asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ICallbackfn)) {
                return new Proxy(obj);
            }
            return (ICallbackfn) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = code;
            if (i == 1) {
                Parcel parcel = reply;
                data.enforceInterface(DESCRIPTOR);
                notifyEvt(data.readInt(), data.readInt(), data.readInt(), data.createByteArray(), data.readString());
                reply.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ICallbackfn {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void notifyEvt(int iEvtMsgid, int wParam, int lParam, byte[] byData, String strData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(iEvtMsgid);
                    _data.writeInt(wParam);
                    _data.writeInt(lParam);
                    _data.writeByteArray(byData);
                    _data.writeString(strData);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
