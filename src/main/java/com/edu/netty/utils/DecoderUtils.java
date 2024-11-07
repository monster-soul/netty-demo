package com.edu.netty.utils;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;

import java.util.List;

public class DecoderUtils extends ByteToMessageDecoder {

    private byte[] head;
    private final int maxLength;
    private final boolean stripDelimiter;

    public DecoderUtils(byte[] head, int maxlenth) {
        this(head, maxlenth, true);
    }


    public DecoderUtils(byte[] head, int maxlenth, boolean stripDelimiter) {
        this.head = head;
        this.maxLength = maxlenth;
        this.stripDelimiter = stripDelimiter;
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            Object decoded = this.decode(ctx, in);
            if (decoded != null) {
                out.add(decoded);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf buffer) {
        byte[] dataHead = new byte[this.head.length];
        for (int i = 0; i < head.length; i++) {
            int readIndex = buffer.readerIndex() + i;
            dataHead[i] = buffer.getByte(readIndex);
        }
        if (checkHead(dataHead)) {
            ByteBuf frame = null;
            byte[] th = new byte[]{buffer.getByte(buffer.readerIndex() + dataHead.length),
                    buffer.getByte(buffer.readerIndex() + dataHead.length + 1)};
            int length = InstructionByteUtils.byteArrayToShort(th, 0, true);
            if ((length + 1) > buffer.readableBytes()) {
                return null;
            }
            if (this.stripDelimiter) {
                frame = buffer.readRetainedSlice(length);
                buffer.skipBytes(1);
            } else {
                frame = buffer.readRetainedSlice(length + 1);
            }
            return frame;
        } else {
            return this.decode(ctx, buffer, 0);
        }
    }

    public boolean checkHead(byte[] dataHead) {
        for (int i = 0; i < head.length; i++) {
            if (head[i] != dataHead[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean failFast = true;
    private boolean discarding;
    private int discardedBytes;
    private int offset;
    private int spiltMinLenth;
    private int minLenth = 3;

    public Object decode(ChannelHandlerContext ctx, ByteBuf buffer, int oldlenth) {
        try {
            boolean minlenthB = true;
            int eol = this.findEndOfLine(buffer, oldlenth);
            int length;
            if (eol <= 0 && oldlenth > 0) {
                eol = buffer.readerIndex() + oldlenth - 1;
                minlenthB = false;
            }
            if (!this.discarding) {
                if (eol >= 0) {
                    length = eol - buffer.readerIndex();
                    if (length > minLenth && length < spiltMinLenth && minlenthB) {
                        return this.decode(ctx, buffer, length + 1);
                    }
                    int delimLength = buffer.getByte(eol) == 13 ? 2 : 1;
                    if (length > this.maxLength) {
                        buffer.readerIndex(eol + delimLength);
                        this.fail(ctx, length);
                        return null;
                    } else {
                        ByteBuf frame;
                        if (this.stripDelimiter) {
                            frame = buffer.readRetainedSlice(length);
                            buffer.skipBytes(delimLength);
                        } else {
                            frame = buffer.readRetainedSlice(length + delimLength);
                        }

                        return frame;
                    }
                } else {
                    length = buffer.readableBytes();
                    if (length > this.maxLength) {
                        this.discardedBytes = length;
                        buffer.readerIndex(buffer.writerIndex());
                        this.discarding = true;
                        this.offset = 0;
                        if (this.failFast) {
                            this.fail(ctx, "over " + this.discardedBytes);
                        }
                    }

                    return null;
                }
            } else {
                if (eol >= 0) {
                    length = this.discardedBytes + eol - buffer.readerIndex();
                    length = buffer.getByte(eol) == 13 ? 2 : 1;
                    buffer.readerIndex(eol + length);
                    this.discardedBytes = 0;
                    this.discarding = false;
                    if (!this.failFast) {
                        this.fail(ctx, length);
                    }
                } else {
                    this.discardedBytes += buffer.readableBytes();
                    buffer.readerIndex(buffer.writerIndex());
                    this.offset = 0;
                }

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void fail(ChannelHandlerContext ctx, int length) {
        this.fail(ctx, String.valueOf(length));
    }

    public void fail(ChannelHandlerContext ctx, String length) {
        ctx.fireExceptionCaught(new TooLongFrameException("frame length (" + length + ") exceeds the allowed maximum (" + this.maxLength + ')'));
    }

    public int findEndOfLine(ByteBuf buffer, int start) {
        int totalLength = buffer.readableBytes();
        int i = buffer.forEachByte(buffer.readerIndex() + start, totalLength - this.offset, ByteProcessor.FIND_LF);
        if (i >= 0) {
            this.offset = 0;
            if (i > 0 && buffer.getByte(i - 1) == 13) {
                --i;
            }
        } else {
            this.offset = totalLength;
        }

        return i;
    }
}
