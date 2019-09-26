import java.util.*;
import java.io.*;

/// ���̕�����i�����j���𐶐����A�n�b�V��������Ɣ�r�������s���N���X
public class SearchClearText {
    /// �����ςݕ����̏o�͗p������i�f�o�b�O�p�j
    private String clearTextList = "";
    private boolean output_clearTextList;

    /// �}���`�X���b�h�����̉�
    private boolean userMultiThread;

    /// �n�b�V�������p�N���X�̃C���X�^���X
    private ComputeHash computeHash = new ComputeHash();

    /// �e�X���b�h�����I�������ʕ�����
    private String[] resultStr;
    private byte[] targetHashedBytes;

    /// ���̕�����̌��
    private byte[][] srcStr;

    /// ���̕�����̌��i����O�j
    private byte[][] chr;

    /// �I�������X���b�h���̃C���f�b�N�X�ԍ�
    private int selectIndex;

    /// �����Ώە����̃R�[�h���i�[����z��
    private byte[] targetChars;
    
    /// �����͈͐擪����
    private int[][] chrStart;

    /// �����͈͖�������
    private int[][] chrEnd;

    /// �I�������A���S���Y���̃C���f�b�N�X�ԍ�
    private int Algorithm_Index;

    public SearchClearText(int alg_index, String targetStr, int strLen, int threadMax, int mode, boolean arg_enableMultiThread, boolean enableDebug) {
        output_clearTextList = enableDebug;
        userMultiThread = arg_enableMultiThread;
        srcStr = new byte[threadMax][];
        chr = new byte[threadMax][];

        for (int i = 0; i < threadMax; i++) {
            srcStr[i] = new byte[strLen];
            chr[i] = new byte[strLen];
        }

        // �I�������A���S���Y���̃C���f�b�N�X�ԍ�
        Algorithm_Index = alg_index;

        // �I�������X���b�h���̃C���f�b�N�X�ԍ��̎w��i�z��̑I��)
        switch (threadMax) {
        case 1:
            selectIndex = 0;
            break;
        case 2:
            selectIndex = 1;
            break;
        case 4:
            selectIndex = 2;
            break;
        case 8:
            selectIndex = 3;
            break;
        case 16:
            selectIndex = 4;
            break;
        default:
            System.out.println("threadMax = " + Integer.toString(threadMax) +   ", threadMax is Invalid...");
            System.exit(-1);
            break;
        }

        // �n�b�V����̌����Ώە�������Z�b�g
        targetHashedBytes = new byte[targetStr.length()/2];
        for (int i = 0; i < targetStr.length(); i += 2) {
            targetHashedBytes[i/2] = (byte)(16*charToHex(targetStr.charAt(i)) + charToHex(targetStr.charAt(i+1)));
        }

        // �����͈͔z��̏�����
        targetChars_Init(mode);
    }

    //-----------------------------------------------------------------------------//
    // �����Ώە�����z��ɃZ�b�g����B
    //-----------------------------------------------------------------------------//
    private void targetChars_Init(int mode) {
        // �����͈͔z��̏�����
        chr_StartEnd_Init();

        switch (mode) {
            case 0: {
                // �p����������ыL�����Ώۂ̂Ƃ�
                //targetChars = new int[0xff - 0x00];
                targetChars = new byte[0x7f - 0x20];

                int i = 0;
                for (byte num = 0x20; num < 0x7f; num++, i++)
                {
                    targetChars[i] = num;
                }
                break;
            }
            case 1: {
                // �p�������݂̂��Ώۂ̂Ƃ�
                targetChars = new byte[('9' - '0') + 1 + ('Z' - 'A') + 1 + ('z' - 'a') + 1];

                int i = 0;
                for (byte num = (byte)'0'; num <= (byte)'9'; num++, i++)
                {
                    targetChars[i] = num;
                }

                for (byte num = (byte)'A'; num <= (byte)'Z'; num++, i++)
                {
                    targetChars[i] = num;
                }

                for (byte num = (byte)'a'; num <= (byte)'z'; num++, i++)
                {
                    targetChars[i] = num;
                }
                break;
            }
        }

        // �����͈͔z��̏�����
        chr_StartEnd_Set();
    }

    //-----------------------------------------------------------------------------//
    // �����͈͔z��̏�����
    //-----------------------------------------------------------------------------//
    private void chr_StartEnd_Init() {
        chrStart = new int[5][];
        chrEnd = new int[5][];

        chrStart[0] = new int[1];
        chrStart[1] = new int[2];
        chrStart[2] = new int[4];
        chrStart[3] = new int[8];
        chrStart[4] = new int[16];

        chrEnd[0] = new int[1];
        chrEnd[1] = new int[2];
        chrEnd[2] = new int[4];
        chrEnd[3] = new int[8];
        chrEnd[4] = new int[16];
    }

    //-----------------------------------------------------------------------------//
    // �e�X���b�h���Ƃ̑Ώ۔͈͂̃Z�b�g
    //-----------------------------------------------------------------------------//
    private void chr_StartEnd_Set() {
        //-------------------------------------------------------------------------//
        // �X���b�h�����P�̂Ƃ��̊J�n�E�I������
        //-------------------------------------------------------------------------//
        chrStart[0][0] = 0;
        chrEnd[0][0] = targetChars.length;

        //-------------------------------------------------------------------------//
        // �X���b�h�����Q�i�z��C���f�b�N�X=1�j�̂Ƃ��̊J�n�E�I������
        //-------------------------------------------------------------------------//
        chrStart[1][0] = 0;
        chrStart[1][1] = targetChars.length / 2;
        chrEnd[1][0] = chrStart[1][1];
        chrEnd[1][1] = targetChars.length;

        //-------------------------------------------------------------------------//
        // �X���b�h�����S�i�z��C���f�b�N�X=2�j�̂Ƃ��̊J�n�E�I������
        //-------------------------------------------------------------------------//
        chrStart[2][0] = 0;
        chrStart[2][1] = 1 * targetChars.length / 4;
        chrStart[2][2] = 2 * targetChars.length / 4;
        chrStart[2][3] = 3 * targetChars.length / 4;
        chrEnd[2][0] = chrStart[2][1];
        chrEnd[2][1] = chrStart[2][2];
        chrEnd[2][2] = chrStart[2][3];
        chrEnd[2][3] = targetChars.length;

        //-------------------------------------------------------------------------//
        // �X���b�h�����W�i�z��C���f�b�N�X=3�j�̂Ƃ��̊J�n�E�I������
        //-------------------------------------------------------------------------//
        chrStart[3][0] = 0;
        chrStart[3][1] = 1 * targetChars.length / 8;
        chrStart[3][2] = 2 * targetChars.length / 8;
        chrStart[3][3] = 3 * targetChars.length / 8;
        chrStart[3][4] = 4 * targetChars.length / 8;
        chrStart[3][5] = 5 * targetChars.length / 8;
        chrStart[3][6] = 6 * targetChars.length / 8;
        chrStart[3][7] = 7 * targetChars.length / 8;
        chrEnd[3][0] = chrStart[3][1];
        chrEnd[3][1] = chrStart[3][2];
        chrEnd[3][2] = chrStart[3][3];
        chrEnd[3][3] = chrStart[3][4];
        chrEnd[3][4] = chrStart[3][5];
        chrEnd[3][5] = chrStart[3][6];
        chrEnd[3][6] = chrStart[3][7];
        chrEnd[3][7] = targetChars.length;

        //-------------------------------------------------------------------------//
        // �X���b�h�����P�U�i�z��C���f�b�N�X=4�j�̂Ƃ��̊J�n�E�I������
        //-------------------------------------------------------------------------//
        chrStart[4][0] = 0;
        chrStart[4][1] = 1 * targetChars.length / 16;
        chrStart[4][2] = 2 * targetChars.length / 16;
        chrStart[4][3] = 3 * targetChars.length / 16;
        chrStart[4][4] = 4 * targetChars.length / 16;
        chrStart[4][5] = 5 * targetChars.length / 16;
        chrStart[4][6] = 6 * targetChars.length / 16;
        chrStart[4][7] = 7 * targetChars.length / 16;
        chrStart[4][8] = 8 * targetChars.length / 16;
        chrStart[4][9] = 9 * targetChars.length / 16;
        chrStart[4][10] = 10 * targetChars.length / 16;
        chrStart[4][11] = 11 * targetChars.length / 16;
        chrStart[4][12] = 12 * targetChars.length / 16;
        chrStart[4][13] = 13 * targetChars.length / 16;
        chrStart[4][14] = 14 * targetChars.length / 16;
        chrStart[4][15] = 15 * targetChars.length / 16;
        chrEnd[4][0] = chrStart[4][1];
        chrEnd[4][1] = chrStart[4][2];
        chrEnd[4][2] = chrStart[4][3];
        chrEnd[4][3] = chrStart[4][4];
        chrEnd[4][4] = chrStart[4][5];
        chrEnd[4][5] = chrStart[4][6];
        chrEnd[4][6] = chrStart[4][7];
        chrEnd[4][7] = chrStart[4][8];
        chrEnd[4][8] = chrStart[4][9];
        chrEnd[4][9] = chrStart[4][10];
        chrEnd[4][10] = chrStart[4][11];
        chrEnd[4][11] = chrStart[4][12];
        chrEnd[4][12] = chrStart[4][13];
        chrEnd[4][13] = chrStart[4][14];
        chrEnd[4][14] = chrStart[4][15];
        chrEnd[4][15] = targetChars.length;
    }

    //-------------------------------------------------------------------//
    // �J�n�ʒu�A�I���ʒu�̊m�F
    //-------------------------------------------------------------------//
    private void display_chrStartEnd() {
        System.out.println("");
        System.out.println("targetChars.length = " + targetChars.length);

        for (int rows = 0; rows < chrStart.length; rows++) {
            for (int th = 0; th < chrStart[rows].length; th++) {
                System.out.println("chrStart[" + rows + "][" + th + "] = " + chrStart[rows][th]);
                System.out.println("chrEnd[" + rows + "][" + th + "] = " + chrEnd[rows][th]);
            }
        }

        for (int i = 0; i < targetChars.length; i++) {
            System.out.println("targetChars[" + i + "] = " + String.format("%x", targetChars[i]));
        }
    }

    //-------------------------------------------------------------------//
    // ���̕����񑍓����茟��
    //-------------------------------------------------------------------//
    public String Get_ClearText(int threadMax) {
        //-------------------------------------------------------------------------//
        // ������""���ǂ����𔻒肷��B
        //-------------------------------------------------------------------------//
        if (Arrays.equals(targetHashedBytes, computeHash.ComputeHash_Common(Algorithm_Index, "".getBytes()))) {
            return "";
        }

        //-------------------------------------------------------------------------//
        // �������P�����ȏ�̕�����̏ꍇ
        //-------------------------------------------------------------------------//
        resultStr = new String[threadMax];

        if (userMultiThread) {
            //---------------------------------------------------------------------//
            // �}���`�X���b�h�����̏ꍇ
            //---------------------------------------------------------------------//
            class MyThread extends Thread {
                int threadNum;
                public MyThread(int threadNum) {
                    this.threadNum = threadNum;
                }
                @Override
                public void run() {
                    thread_func(threadNum, resultStr);
                }
            }

            MyThread[] th = new MyThread[threadMax];
            for (int threadNum = 0; threadNum < threadMax; threadNum++) {
                th[threadNum] = new MyThread(threadNum);
                th[threadNum].start();
            }

            for (int threadNum = 0; threadNum < threadMax; threadNum++) {
                try {
                    th[threadNum].join();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }

        } else {
            //---------------------------------------------------------------------//
            // ������s�̏ꍇ
            //---------------------------------------------------------------------//
            for (int threadNum = 0; threadNum < threadMax; threadNum++) {
                thread_func(threadNum, resultStr);
            }
        }

        //-------------------------------------------------------------------------//
        // �w�蕶�����ł̌��ʕ�
        //-------------------------------------------------------------------------//
        while (true) {
            int resultCount = 0;

            for (int i = 0; i < threadMax; i++) {
                if (resultStr[i] != null) {
                    if (resultStr[i] != "") {
                        // �f�o�b�O�p
                        if (output_clearTextList)
                            save_clearTextList();

                        // �����ꂩ�̃X���b�h���������Ԃ��Ă����ꍇ�i���������ꍇ�j
                        return resultStr[i];
                    } else {
                        resultCount++;

                        if (resultCount >= threadMax) {
                            // �f�o�b�O�p
                            if (output_clearTextList)
                                save_clearTextList();

                            // ���ׂ�""�������ꍇ�i������Ȃ������ꍇ�j
                            return null;
                        }
                    }
                }
            }
        }
    }

    private void thread_func(int threadNum, String[] resultStr) {
        // �w�肵���A���S���Y���ɂăn�b�V���l�𐶐�����B
        if (Get_NextClearText_Group_All(threadNum, 0)) {
            //-----------------------------------------------------------//
            // �����n�b�V���l�������ł��錳�̕����񂪌��������ꍇ
            //-----------------------------------------------------------//
            String ClearText = "";
            for (int i = 0; i < srcStr[threadNum].length; i++) {
                ClearText += (char)srcStr[threadNum][i];
            }
            resultStr[threadNum] = ClearText;
        } else {
            resultStr[threadNum] = "";
        }
    }

    //-------------------------------------------------------------------//
    // ���������������X�g�̃t�@�C���ւ̕ۑ�
    //-------------------------------------------------------------------//
    private void save_clearTextList() {
        try {
            // FileWriter�N���X�̃I�u�W�F�N�g�𐶐�����
            FileWriter file = new FileWriter("ClearTextList_" + srcStr[0].length + ".txt");
            // PrintWriter�N���X�̃I�u�W�F�N�g�𐶐�����
            PrintWriter pw = new PrintWriter(new BufferedWriter(file));
            
            //�t�@�C���ɏ�������
            pw.println(clearTextList);
            
            //�t�@�C�������
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //-------------------------------------------------------------------//
    // ���Y�K�w�̕������𐶐����n�b�V���l�Ɣ�r����B
    // ������Ȃ���Ύ��̊K�w�ցB
    //-------------------------------------------------------------------//
    protected boolean Get_NextClearText_Group_All(int threadNum, int target_strLength) {
        // ������̒����̏���𒴂����ꍇ�͒��~����B
        if (target_strLength > chr[threadNum].length - 1) {
            return (false);
        }

        //srcStr[threadNum] = new byte[target_strLength + 1];
        srcStr[threadNum] = chr[threadNum];

        // �܂��͕�����target_strLength�̌����`�F�b�N
        for (int index = chrStart[selectIndex][threadNum]; index < chrEnd[selectIndex][threadNum]; index++) {
            chr[threadNum][target_strLength] = targetChars[index];
            srcStr[threadNum][target_strLength] = chr[threadNum][target_strLength];

            // �f�o�b�O�p�o��
            if (output_clearTextList)
                clearTextList += "\"" + (new String(srcStr[threadNum])) + "\"\r\n";

            // �w�肵���A���S���Y���ɂăn�b�V���l�𐶐�����B
            if (Arrays.equals(targetHashedBytes, computeHash.ComputeHash_Common(Algorithm_Index, srcStr[threadNum]))) {
                return (true);
            }
        }

        // ������target_strLength + 1�̌����`�F�b�N
        for (int index = chrStart[selectIndex][threadNum]; index < chrEnd[selectIndex][threadNum]; index++) {
            chr[threadNum][target_strLength] = targetChars[index];

            if (Get_NextClearText_Group_All_level2(threadNum, target_strLength + 1)) {
                return (true);
            }
        }

        return (false);
    }

    //-------------------------------------------------------------------//
    // ���Y�K�w�̕������𐶐����n�b�V���l�Ɣ�r����B
    // ������Ȃ���Ύ��̊K�w�ցB
    //-------------------------------------------------------------------//
    protected boolean Get_NextClearText_Group_All_level2(int threadNum, int target_strLength) {
        // ������̒����̏���𒴂����ꍇ�͒��~����B
        if (target_strLength > chr[threadNum].length - 1) {
            return (false);
        }

        srcStr[threadNum] = new byte[target_strLength + 1];

        for (int col = 0; col < target_strLength; col++) {
            srcStr[threadNum][col] = chr[threadNum][col];
        }

        // �܂��͕�����target_strLength�̌����`�F�b�N
        for (int index = chrStart[0][0]; index < chrEnd[0][0]; index++) {
            chr[threadNum][target_strLength] = targetChars[index];
            srcStr[threadNum][target_strLength] = (byte)chr[threadNum][target_strLength];

            // �f�o�b�O�p�o��
            if (output_clearTextList)
                clearTextList += "\"" + (new String(srcStr[threadNum])) + "\"\r\n";

            // �w�肵���A���S���Y���ɂăn�b�V���l�𐶐�����B
            if (Arrays.equals(targetHashedBytes, computeHash.ComputeHash_Common(Algorithm_Index, srcStr[threadNum]))) {
                return (true);
            }
        }

        // ������target_strLength + 1�̌����`�F�b�N
        for (int index = chrStart[0][0]; index < chrEnd[0][0]; index++) {
            chr[threadNum][target_strLength] = targetChars[index];

            if (Get_NextClearText_Group_All_level2(threadNum, target_strLength + 1)) {
                return (true);
            }
        }

        return (false);
    }

    //-------------------------------------------------------------------//
    // �����������̕��������\������B
    //-------------------------------------------------------------------//
    public void display_ClearText() {
        String[] clearText = new String[srcStr.length];

        for (int thread = 0; thread < srcStr.length; thread++)
        {
            // �X���b�h���Ƃɓr���o�ߕ�������擾

            clearText[thread] = "";
            for (int i = 0; i < srcStr[thread].length; i++)
            {
                clearText[thread] += (char)srcStr[thread][i];
            }

            if (clearText[thread].indexOf("\0") >= 0)
            {
                // �o�͐�e�L�X�g�{�b�N�X�ɏo��
                System.out.println("�X���b�h" + thread + ": (�N���҂�)");
            }
            else if (resultStr[thread] == "")
            {
                // �o�͐�e�L�X�g�{�b�N�X�ɏo��
                System.out.println("�X���b�h" + thread + ": (�����I��)");
            }
            else
            {
                int threadCount = srcStr.length;
                //double progress = ((double)(srcStr[thread][0] - targetChars[chrStart[selectIndex][thread]]) / (double)(chrEnd[selectIndex][thread] - chrStart[selectIndex][thread])) * 100;
                double progress = ((double)(get_index_targetChars(srcStr[thread][0]) - get_index_targetChars(targetChars[chrStart[selectIndex][thread]])) / (double)(chrEnd[selectIndex][thread] - chrStart[selectIndex][thread])) * 100;

                // �o�͐�e�L�X�g�{�b�N�X�ɏo��
                System.out.print("�X���b�h" + thread + "  (" + String.format("%.0f", progress) + "% �I��)  :  " + clearText[thread]);
            }

            if (thread % 2 == 0)
            {
                System.out.print("\t");
            }
            else
            {
                System.out.println();
            }
        }
    }

    //-------------------------------------------------------------------//
    // �w�肵������(byte�^)���AtargetChars[]�̉��Ԗڂ��𒲂ׂ�
    //-------------------------------------------------------------------//
    private int get_index_targetChars(byte val) {
        int i;

        for (i = 0; i < targetChars.length; i++) {
            if (val == targetChars[i]) {
                return i;
            }
        }

        return 0;
    }

    //-------------------------------------------------------------------//
    // 16�i��������𐔒l�ɕϊ�����
    //-------------------------------------------------------------------//
    private byte charToHex(char s) {
        if (0x30 <= s && s <= 0x39) {
            return (byte)(s - 0x30);
        } else if (0x41 <= s && s <= 0x46) {
            return (byte)(s - 55);
        } else if (0x61 <= s && s <= 0x66) {
            return (byte)(s - 87);
        }

        System.out.println("charToHex() Error ...\n"+
            s +  " is wrong.\n");
        return (byte)0xff;
    }
}
