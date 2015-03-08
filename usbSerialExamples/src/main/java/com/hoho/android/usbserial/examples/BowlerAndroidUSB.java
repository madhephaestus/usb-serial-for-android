package com.hoho.android.usbserial.examples;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import java.nio.ByteBuffer;

/**
 * Created by hephaestus on 3/8/15.
 */
public class BowlerAndroidUSB extends BowlerAbstractConnection{
    private UsbSerialPort sPort;

    private static final int READ_WAIT_MILLIS = 200;
    private static final int BUFSIZ = 4096;

    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFSIZ);

    // Synchronized by 'mWriteBuffer'
    private final ByteBuffer mWriteBuffer = ByteBuffer.allocate(BUFSIZ);

    public BowlerAndroidUSB(UsbSerialPort sPort) {
        this.sPort =sPort;

    }

    public boolean connect(){
        setConnected(true);
        return isConnected();
    }

	/**
	 * Gets the data ins.
	 *
	 * @return the data ins
	 */
	@Override
	public DataInputStream getDataIns() throws NullPointerException{
		new RuntimeException("This method should not be called").printStackTrace();
		while(true);
	}

	/**
	 * Gets the data outs.
	 *
	 * @return the data outs
	 */
	@Override
	public DataOutputStream getDataOuts() throws NullPointerException{
		new RuntimeException("This method should not be called").printStackTrace();
		while(true);
	}

	/**
	 * Write.
	 *
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	//private ByteList outgoing = new ByteList();
	public void write(byte[] data) throws IOException {
        // Handle outgoing data.
        int len;
        byte[] outBuff = null;
        synchronized (mWriteBuffer) {
            len = mWriteBuffer.position();
            if (len > 0) {
                outBuff = new byte[len];
                mWriteBuffer.rewind();
                mWriteBuffer.get(outBuff, 0, len);
                mWriteBuffer.clear();
            }
        }
        if (outBuff != null) {
            sPort.write(outBuff, READ_WAIT_MILLIS);
        }

	}

	@Override
	public boolean loadPacketFromPhy(ByteList bytesToPacketBuffer) throws NullPointerException, IOException{
                // Handle incoming data.
        int len = sPort.read(mReadBuffer.array(), READ_WAIT_MILLIS);
        if (len > 0) {

            final byte[] data = new byte[len];
            mReadBuffer.get(data, 0, len);

            bytesToPacketBuffer.add(data);
            mReadBuffer.clear();

		    BowlerDatagram bd = BowlerDatagramFactory.build(bytesToPacketBuffer);
			if (bd!=null) {
//				Log.info("\nR<<"+bd);
				onDataReceived(bd);

				return true;
			}

        }

		return false;
	}
    public boolean reconnect(){
        return false;
    }

    public boolean waitingForConnection(){
        return true;
    }

}
