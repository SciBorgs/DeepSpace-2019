package main.java.frc.robot.subsystems;

import org.usfirst.frc.team1155.robot.Robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import java.util.Arrays;

public class RetroreflectiveTapeSubsystem extends Subsystem {

    public NetworkTable getTable() {
        return Robot.limelight.getCameraTable();
    }

    public double get(NetworkTable table, String variable) {
        return Robot.limelight.getTableDat(table, variable);
    }

    public double[] center() {
        NetworkTable table = getTable();
        double[][] allvalues = { { get(table, "tx0"), get(table, "ty0") }, { get(table, "tx1"), get(table, "ty1") },
                { get(table, "tx2"), get(table, "ty2") } };
        double[] xvalues = { allvalues[0][0], allvalues[0][0], allvalues[0][0] };
        Arrays.sort(xvalues);

        double angle = 0.0;
        double[] finalavg;

        for (int i = 0; i < 3; i++) {
            if (allvalues[i][0] == xvalues[1]) {
                angle = (get(table, "ts" + Integer.toString(i)));
            }
        }

        // left
        if (angle > -45) {
            double avgx = (xvalues[1] + xvalues[0]) / 2.0;
            double avgy = 0;
            for (int i = 0; i < 3; i++) {
                if ((allvalues[i][0] == xvalues[1]) || (allvalues[i][0] == xvalues[0])) {
                    avgy = avgy + allvalues[i][1];
                }
            }
            avgy = avgy / 2.0;
            finalavg = new double[] { avgx, avgy };
            return finalavg;
        }

        // right
        else if (angle < -45) {
            double avgx = (xvalues[1] + xvalues[2]) / 2.0;
            double avgy = 0;
            for (int i = 0; i < 3; i++) {
                if ((allvalues[i][0] == xvalues[1]) || (allvalues[i][0] == xvalues[2])) {
                    avgy = avgy + allvalues[i][1];
                }
            }
            avgy = avgy / 2.0;
            finalavg = new double[] { avgx, avgy };
            return finalavg;
        }

        return finalavg;
    }

    @Override
    protected void initDefaultCommand() {
        // LITTERALLY DIE
    }
}