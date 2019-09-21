import java.util.*;
import java.io.*;

/// ���̕�����i�����j���𐶐����A�n�b�V��������Ɣ�r�������s���N���X
public class SearchClearText_debug {
    /// �����ςݕ����̏o�͗p������i�f�o�b�O�p�j
    private String clearTextList = "";
    private boolean output_clearTextList = true;

    /// �n�b�V�������p�N���X�̃C���X�^���X
    private ComputeHash computeHash = new ComputeHash();

    /// �e�X���b�h�����I�������ʕ�����
    private String[] resultStr;
    private String target_HashedStr;

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

    public SearchClearText_debug(int alg_index, String targetStr, int strLen, int threadMax, int mode) {
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
        target_HashedStr = targetStr;

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
        if (target_HashedStr == computeHash.ComputeHash_Common(Algorithm_Index, "".getBytes())) {
            return "";
        }

        //-------------------------------------------------------------------------//
        // �������P�����ȏ�̕�����̏ꍇ
        //-------------------------------------------------------------------------//
        String[] resultStr = new String[threadMax];

        //---------------------------------------------------------------------//
        // �X���b�h����
        //---------------------------------------------------------------------//

        Collection<Integer> elems = new LinkedList<Integer>();
        for (int i = 0; i < threadMax; ++i) {
            elems.add(i);
        }

        Parallel.For(elems, 
        // The operation to perform with each item
        new Parallel.Operation<Integer>() {

            public void perform(Integer threadNum) {
                // �w�肵���A���S���Y���ɂăn�b�V���l�𐶐�����B
                if (Get_NextClearText_Group_All(threadNum, srcStr, chr, 0)) {
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
            };

        });

        //-------------------------------------------------------------------------//
        // ���ׂẴX���b�h�����ʂ�Ԃ��܂őҋ@����B
        //-------------------------------------------------------------------------//
        while (true) {
            int resultCount = 0;

            try {
                Thread.sleep(500);
            } catch (Exception e) {

            }

            for (int i = 0; i < threadMax; i++) {
                if (resultStr[i] != null) {
                    if (resultStr[i] != "") {
                        if (output_clearTextList) save_clearTextList();

                        // �����ꂩ�̃X���b�h���������Ԃ��Ă����ꍇ�i���������ꍇ�j
                        return resultStr[i];
                    } else {
                        resultCount++;

                        if (resultCount >= threadMax) {
                            if (output_clearTextList) save_clearTextList();

                            // ���ׂ�""�������ꍇ�i������Ȃ������ꍇ�j
                            return null;
                        }
                    }
                }
            }
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
    protected boolean Get_NextClearText_Group_All(int threadNum, byte[][] targetArray, byte[][] chr, int i) {
        // ������̒����̏���𒴂����ꍇ�͒��~����B
        if (i > chr[threadNum].length - 1) {
            return (false);
        }

        //targetArray[threadNum] = new byte[i + 1];
        targetArray[threadNum] = chr[threadNum];

        // �܂��͕�����i�̌����`�F�b�N
        for (int index = chrStart[selectIndex][threadNum]; index < chrEnd[selectIndex][threadNum]; index++) {
            chr[threadNum][i] = targetChars[index];
            targetArray[threadNum][i] = chr[threadNum][i];

            // �f�o�b�O�p�o��
            if (output_clearTextList)
                clearTextList += "\"" + (new String(targetArray[threadNum])) + "\"\r\n";

            // �w�肵���A���S���Y���ɂăn�b�V���l�𐶐�����B
            if (target_HashedStr == computeHash.ComputeHash_Common(Algorithm_Index, targetArray[threadNum])) {
                return (true);
            }
        }

        // ������i + 1�̌����`�F�b�N
        for (int index = chrStart[selectIndex][threadNum]; index < chrEnd[selectIndex][threadNum]; index++) {
            chr[threadNum][i] = targetChars[index];

            if (Get_NextClearText_Group_All_level2(threadNum, targetArray, chr, i + 1)) {
                return (true);
            }
        }

        return (false);
    }

    //-------------------------------------------------------------------//
    // ���Y�K�w�̕������𐶐����n�b�V���l�Ɣ�r����B
    // ������Ȃ���Ύ��̊K�w�ցB
    //-------------------------------------------------------------------//
    protected boolean Get_NextClearText_Group_All_level2(int threadNum, byte[][] targetArray, byte[][] chr, int i) {
        // ������̒����̏���𒴂����ꍇ�͒��~����B
        if (i > chr[threadNum].length - 1) {
            return (false);
        }

        targetArray[threadNum] = new byte[i + 1];

        for (int col = 0; col < i; col++) {
            targetArray[threadNum][col] = chr[threadNum][col];
        }

        // �܂��͕�����i�̌����`�F�b�N
        for (int index = chrStart[0][0]; index < chrEnd[0][0]; index++) {
            chr[threadNum][i] = targetChars[index];
            targetArray[threadNum][i] = (byte)chr[threadNum][i];

            // �f�o�b�O�p�o��
            if (output_clearTextList)
                clearTextList += "\"" + (new String(targetArray[threadNum])) + "\"\r\n";

            // �w�肵���A���S���Y���ɂăn�b�V���l�𐶐�����B
            if (target_HashedStr == computeHash.ComputeHash_Common(Algorithm_Index, targetArray[threadNum])) {
                return (true);
            }
        }

        // ������i + 1�̌����`�F�b�N
        for (int index = chrStart[0][0]; index < chrEnd[0][0]; index++) {
            chr[threadNum][i] = targetChars[index];

            if (Get_NextClearText_Group_All_level2(threadNum, targetArray, chr, i + 1)) {
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
}
