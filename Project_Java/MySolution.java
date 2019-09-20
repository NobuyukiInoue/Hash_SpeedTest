import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class MySolution {
    private Date startTime;
    private int ClearTextMaxLength;

    public void Main(String open_FileName, int thread_count, int search_max_length, int search_mode) {
        // �n�b�V�������񂪕ۑ����ꂽ�t�@�C���̓ǂݍ���
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(open_FileName));
        String string = reader.readLine();
        while (string != null){
            builder.append(string + System.getProperty("line.separator"));
            string = reader.readLine();
        }
        String read_Text = builder.toString();

        // �R�����g���̍폜
        read_Text = read_Text.replaceAll("#.*\n", "").replaceAll("//.*\n", "");

        // �n�b�V���A���S���Y���ƃn�b�V��������̕���
        String[] flds = read_Text.trim().split(":");
        String algorithm = flds[0];
        String target_hashed_text = flds[1].trim();

        // �������镽���̍ő啶����
        ClearTextMaxLength = search_max_length;

        System.out.println("===============================================================");
        System.out.println("algorithm          : " + algorithm);
        System.out.println("target Hashed Text : " + target_hashed_text);
        System.out.println("thread count       : " + Integer.toString(thread_count));
        System.out.println("search max length  : " + Integer.toString(search_max_length));
        System.out.println("===============================================================");

        long start = System.currentTimeMillis();

        // �������茟�����s
        search(target_hashed_text, algorithm, thread_count, ClearTextMaxLength, search_mode);

        long end = System.currentTimeMillis();
        System.out.println("Total Execute time ... " + (end - start)  + "ms\n");
    }

    //-----------------------------------------------------------------------------//
    // ���̕����������
    //-----------------------------------------------------------------------------//
    // private async void search(String target_hashed_text, String algorithm, int threadMax, int search_ClearText_MaxLength)
    private void search(String target_hashed_text, String algorithm, int threadMax, int search_ClearText_MaxLength, int search_mode) {
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
        startTime = new Date();

        // ���ʂ̏�����
        String resultStr = "";
        Search_ClearText search_cleartext;

        ComputeHash ch = new ComputeHash();
        ch = null;

        // �P��������w�肵�������񒷂܂Ō�������B
        for (int i = 1; i <= search_ClearText_MaxLength; i++) {
            // �������������p�C���X�^���X�̐���
            search_cleartext = new Search_ClearText(Algorithm_Index, target_hashed_text, i, threadMax, 0);

            // ������i�ł̑������蕽�������J�n������ۑ�
            Date current_startTime = new Date();

            //---------------------------------------------------------------------//
            // ������i�ł̑������蕽�������J�n
            //---------------------------------------------------------------------//
            /*
            await Task.Run(() =>
            {
                // ������̌������J�n
                resultStr = search_cleartext.Get_ClearText(threadMax);
            });
            */
            resultStr = search_cleartext.Get_ClearText(threadMax);

            // �������蕽�������I�������Ƃ̍����擾
        	TimeSpan ts = (new Date()) - startTime;

            //---------------------------------------------------------------------//
            // ������i�ł̑������蕽�������I��
            //---------------------------------------------------------------------//
            if (resultStr != null) {
                /*
                System.out.println("���̕����񂪌�����܂����I\r\n"
                                + "\r\n"
                                + "���� = " + resultStr + "\r\n"
                                + "\r\n"
                                + "��͎��� = " +  ts.toString(@"hh\:mm\:ss\.fff") + " �b");
                */
                System.out.println("���̕����񂪌�����܂����I\r\n"
                                + "\r\n"
                                + "���� = " + resultStr + "\r\n"
                                + "\r\n"
                                + "��͎��� = " +  TimeSpan.toString(ts) + " �b");
                break;
            } else {
                System.out.println(TimeSpan.toString(ts) + " ... " + Integer.toString(i) + "�����̑g�ݍ��킹�ƍ��I��");

                /*
                if (i == 2)
                {
                    // 2���ڂ܂ŏI�������A2���̏������Ԃ���ɗ\�z�I���������Z�o����B
                    TimeSpan oneLengthTime = Date.Now - current_startTime;
                    TimeSpan resultTime;

                    System.out.println("OneLength_T = " + oneLengthTime.toString(@"hh\:mm\:ss\.fff") + " �b");

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

                    System.out.println("�\�z��������(2�����܂ł̏������ԂŎZ�o) : " + resultTime.toString(@"hh\:mm\:ss\.fff") + " �b");

                    if (resultTime == new TimeSpan(0, 0, 0))
                    {
                        //System.out.println("�\�z���ԏ���l����");
                    }
                    else
                    {
                        if (resultTime.Days == 0)
                        {
                            System.out.println("�\�z�������� : " + resultTime.toString(@"hh\:mm\:ss") + " �b");
                        }
                        else
                        {
                            System.out.println("�\�z�������� : " + resultTime.Days + " days " + resultTime.toString(@"hh\:mm\:ss") + " �b");
                        }
                    }
                }
               */
            }
        }
        // �������������p�C���X�^���X���������B
        search_cleartext = null;

        if (resultStr == null) {
            // ������Ȃ������ꍇ
            System.out.println("������܂���ł����B");
        }
    }

    //-----------------------------------------------------------------------------//
    /// �I���\�����Ԃ��Z�o����(�p���̂� ... A-Z, a-z, 0-9)
    //-----------------------------------------------------------------------------//
    private TimeSpan get_finTime_for_alfaNum(TimeSpan dt) {
        TimeSpan[] resultArray = new TimeSpan[ClearTextMaxLength];

        // �e���̗\�z�������Ԃ̏�����
        resultArray[1] = dt;

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultArray[len] = new TimeSpan(0, 0, 0);
        }

        // �e���̗\�z�������Ԃ̎Z�o
        for (int len = 2; len < ClearTextMaxLength; len++) {
            for (int i = 0; i < ('9' - '0') + 1 + ('Z' - 'A') + 1 + ('z' - 'a') + 1; i++) {
                try {
                    resultArray[len] += resultArray[len - 1];
                } catch(Exception e) {
                    return (new TimeSpan(0, 0, 0));
                } finally {
                }
            }
        }

        // �e���̏������Ԃ����Z����
        TimeSpan resultTime = new TimeSpan(0, 0, 0);

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultTime += resultArray[len];
        }

        return resultTime;
    }

    //-----------------------------------------------------------------------------//
    /// �I���\�����Ԃ��Z�o����(�p�� + �L�� ... 0x20 - 0x7f)
    //-----------------------------------------------------------------------------//
    private TimeSpan get_finTime_for_all(TimeSpan dt)
    {
        TimeSpan[] resultArray = new TimeSpan[ClearTextMaxLength];

        // �e���̗\�z�������Ԃ̏�����
        resultArray[1] = dt;

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultArray[len] = new TimeSpan(0, 0, 0);
        }

        // �e���̗\�z�������Ԃ̎Z�o
        for (int len = 2; len < ClearTextMaxLength; len++) {
            for (int i = 0x20; i < 0x7f; i++) {
                try {
                    resultArray[len] += resultArray[len - 1];
                } catch(Exception e) {
                    return new TimeSpan(0, 0, 0);
                }
            }
        }

        // �e���̏������Ԃ����Z����
        TimeSpan resultTime = new TimeSpan(0, 0, 0);

        for (int len = 2; len < ClearTextMaxLength; len++) {
            resultTime += resultArray[len];
        }

        return resultTime;
    }
}
