import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class MySolution {
    private long startTime;
    private int ClearTextMaxLength;

    public void Main(String open_FileName, int thread_count, int search_max_length, int search_mode, boolean use_multiThread, boolean use_debug) {
        // �n�b�V�������񂪕ۑ����ꂽ�t�@�C���̓ǂݍ���
        String read_Text = read_file(open_FileName);

        // �R�����g���̍폜
        /*
        read_Text = read_Text.replaceAll("#.*\\n", "");
        read_Text = read_Text.replaceAll("//.*\\n", "");
        */
        read_Text = read_Text.replaceAll("#.*", "");
        read_Text = read_Text.replaceAll("//.*", "");

        // �n�b�V���A���S���Y���ƃn�b�V��������̕���
        String[] flds = read_Text.trim().split(":");
        String algorithm = flds[0];
        String target_hashed_text = flds[1].trim();

        // �������镽���̍ő啶����
        ClearTextMaxLength = search_max_length;

        System.out.println("=====================================================================================");
        System.out.println("algorithm          : " + algorithm);
        System.out.println("target Hashed Text : " + target_hashed_text);
        if (use_multiThread) {
            System.out.println("thread count       : " + Integer.toString(thread_count));
        } else {
            System.out.println("multiThread        : " + Boolean.toString(use_multiThread));
        }
        System.out.println("search max length  : " + Integer.toString(search_max_length));
        System.out.println("=====================================================================================");

        long start = System.currentTimeMillis();

        // �������茟�����s
        search(target_hashed_text, algorithm, thread_count, ClearTextMaxLength, search_mode, use_multiThread, use_debug);

        long end = System.currentTimeMillis();
        System.out.println("Total Execute time ... " + (end - start)  + "ms\n");
    }

    //-----------------------------------------------------------------------------//
    // �w��t�@�C���̓ǂݍ���
    //-----------------------------------------------------------------------------//
    private String read_file(String open_FileName) {
        StringBuilder builder = new StringBuilder();

        try {
            File fp = new File(open_FileName);
            BufferedReader reader = new BufferedReader(new FileReader(fp));
            String string = reader.readLine();
            while (string != null){
                builder.append(string + System.getProperty("line.separator"));
                string = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(-1);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(-1);
        }

        return builder.toString();
    }

    //-----------------------------------------------------------------------------//
    // ���̕����������
    //-----------------------------------------------------------------------------//
    // private async void search(String target_hashed_text, String algorithm, int threadMax, int searchClearText_MaxLength)
    private void search(String target_hashed_text, String algorithm, int threadMax, int searchClearText_MaxLength, int search_mode, boolean use_multiThread, boolean use_debug) {
        // �g�p����X���b�h���̎w��`�F�b�N
        if ((threadMax != 1)
        && (threadMax != 2)
        && (threadMax != 4)
        && (threadMax != 8)
        && (threadMax != 16)) {
            return;
        }

        String algorithm_upper = algorithm.replace("-", "").toUpperCase();
        int Algorithm_Index;

        switch (algorithm_upper) {
        case "MD5":
            Algorithm_Index = 0;
            break;
        case "SHA1":
            Algorithm_Index = 1;
            break;
        case "SHA256":
            Algorithm_Index = 2;
            break;
        case "SHA386":
            Algorithm_Index = 3;
            break;
        case "SHA512":
            Algorithm_Index = 4;
            break;
        case "RIPED160":
            Algorithm_Index = 5;
            break;
        default:
            Algorithm_Index = 2;  // default .. "SHA256"
            break;
        }

        // ���݂̎������擾
        startTime = System.currentTimeMillis();

        // ���ʂ̏�����
        String resultStr = "";
        SearchClearText searchClearText;
//      SearchClearText_debug searchClearText;

        ComputeHash ch = new ComputeHash();
        TimeFormatter timeformatter = new TimeFormatter();
        ch = null;

        // �P��������w�肵�������񒷂܂Ō�������B
        for (int target_strLen = 1; target_strLen <= searchClearText_MaxLength; target_strLen++) {

            // �������������p�C���X�^���X�̐���
            searchClearText = new SearchClearText(Algorithm_Index, target_hashed_text, target_strLen, threadMax, 0, use_multiThread, use_debug);

            // ������i�ł̑������蕽�������J�n������ۑ�
            long current_startTime = System.currentTimeMillis();

            //---------------------------------------------------------------------//
            // ������i�ł̑������蕽�������J�n
            //---------------------------------------------------------------------//
            resultStr = searchClearText.Get_ClearText(threadMax);

            // �������蕽�������I�������Ƃ̍����擾
            long ts = System.currentTimeMillis() - startTime;

            //---------------------------------------------------------------------//
            // ������i�ł̑������蕽�������I��
            //---------------------------------------------------------------------//
            if (resultStr != null) {
                System.out.println("���̕����񂪌�����܂����I\r\n"
                                + "\r\n"
                                + "���� = " + resultStr + "\r\n"
                                + "\r\n"
                                + "��͎��� = " + timeformatter.format(ts) + " �b");
                break;
            } else {
                System.out.println(timeformatter.format(ts) + " ... " + Integer.toString(target_strLen) + "�����̑g�ݍ��킹�ƍ��I��");

                /*
                if (target_strLen == 2)
                {
                    // 2���ڂ܂ŏI�������A2���̏������Ԃ���ɗ\�z�I���������Z�o����B
                    long oneLengthTime = System.currentTimeMillis() - current_startTime;
                    long resultTime;

                    if (search_mode == 0)
                    {
                        resultTime = get_finTime_for_all(oneLengthTime);
                    }
                    else if (search_mode == 1)
                    {
                        resultTime = get_finTime_for_alfaNum(oneLengthTime);
                    }
                    else
                    {
                        resultTime = get_finTime_for_all(oneLengthTime);
                    }

                    //System.out.println("�\�z��������(2�����܂ł̏������ԂŎZ�o) : " + timefomatter.format(resultTime));
                    System.out.println("guess : " + timeformatter.format(resultTime));
                }
                */
            }
        }
        // �������������p�C���X�^���X���������B
        searchClearText = null;

        if (resultStr == null) {
            // ������Ȃ������ꍇ
            System.out.println("������܂���ł����B");
        }
    }

    //-----------------------------------------------------------------------------//
    /// �I���\�����Ԃ��Z�o����(�p���̂� ... A-Z, a-z, 0-9)
    //-----------------------------------------------------------------------------//
    private long get_finTime_for_alfaNum(long dt) {
        long[] resultArray = new long[ClearTextMaxLength];

        for (int len = 2; len < resultArray.length; len++) {
            resultArray[len] = 0;
        }

        // �e���̗\�z�������Ԃ̏�����
        resultArray[1] = dt;

        // �e���̗\�z�������Ԃ̎Z�o
        for (int len = 2; len < ClearTextMaxLength; len++) {
            for (int i = 0; i < ('9' - '0') + 1 + ('Z' - 'A') + 1 + ('z' - 'a') + 1; i++) {
                try {
                    resultArray[len] += resultArray[len - 1];
                } catch (Exception e) {
                    return 0;
                } finally {

                }
            }
        }

        // �e���̏������Ԃ����Z����
        long resultTime = 0;

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultTime += resultArray[len];
        }

        return resultTime;
    }

    //-----------------------------------------------------------------------------//
    /// �I���\�����Ԃ��Z�o����(�p�� + �L�� ... 0x20 - 0x7f)
    //-----------------------------------------------------------------------------//
    private long get_finTime_for_all(long dt)
    {
        long[] resultArray = new long[ClearTextMaxLength];

        // �e���̗\�z�������Ԃ̏�����
        resultArray[1] = dt;

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultArray[len] = 0;
        }

        // �e���̗\�z�������Ԃ̎Z�o
        for (int len = 2; len < ClearTextMaxLength; len++) {
            for (int i = 0x20; i < 0x7f; i++) {
                try {
                    resultArray[len] += resultArray[len - 1];
                } catch (Exception e) {
                    return 0;
                }
            }
        }

        // �e���̏������Ԃ����Z����
        long resultTime = 0;

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultTime += resultArray[len];
        }

        return resultTime;
    }
}
