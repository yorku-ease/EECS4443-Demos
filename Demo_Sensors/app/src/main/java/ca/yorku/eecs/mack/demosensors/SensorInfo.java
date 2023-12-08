package ca.yorku.eecs.mack.demosensors;

/**
 * SensorInfo - simple class to hold information about a sensor
 *
 * @author (c) Scott MacKenzie
 */
public class SensorInfo
{
    int type;

    String name;

    String value0Label;
    float value0Min;
    float value0Max;

    String value1Label;
    float value1Min;
    float value1Max;

    String value2Label;
    float value2Min;
    float value2Max;

    String value3Label;
    float value3Min;
    float value3Max;

    SensorInfo(int typeArg, String nameArg, String value0LabelArg, float value0MinArg,
               float value0MaxArg, String value1LabelArg, float value1MinArg, float value1MaxArg, String value2LabelArg,
               float value2MinArg, float value2MaxArg, String value3LabelArg, float value3MinArg, float value3MaxArg)
    {
        type = typeArg;
        name = nameArg;

        value0Label = value0LabelArg;
        value0Min = value0MinArg;
        value0Max = value0MaxArg;

        value1Label = value1LabelArg;
        value1Min = value1MinArg;
        value1Max = value1MaxArg;

        value2Label = value2LabelArg;
        value2Min = value2MinArg;
        value2Max = value2MaxArg;

        value3Label = value3LabelArg;
        value3Min = value3MinArg;
        value3Max = value3MaxArg;
    }
}
