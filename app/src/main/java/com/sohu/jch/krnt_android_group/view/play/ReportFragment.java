package com.sohu.jch.krnt_android_group.view.play;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sohu.jch.krnt_android_group.R;
import com.sohu.jch.krnt_android_group.util.CpuMonitor;
import com.sohu.jch.krnt_android_group.util.SharePrefUtil;
import com.sohu.kurento.group.KPeerConnectionClient;

import org.webrtc.StatsReport;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ReportFragment extends Fragment {

    @Bind(R.id.encoder_stat_call)
    TextView encoderStatCall;
    @Bind(R.id.report_name_tv)
    TextView reportNameTv;
    @Bind(R.id.hud_stat_bwe)
    TextView hudStatBwe;
    @Bind(R.id.hud_stat_connection)
    TextView hudStatConnection;
    @Bind(R.id.hud_stat_video_send)
    TextView hudStatVideoSend;
    @Bind(R.id.hud_stat_video_recv)
    TextView hudStatVideoRecv;
    @Bind(R.id.hudview_container)
    TableLayout hudviewContainer;

    private final CpuMonitor cpuMonitor = new CpuMonitor();

    public ReportFragment() {
        // Required empty public constructor
    }

    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    private Map<String, String> getReportMap(StatsReport report) {
        Map<String, String> reportMap = new HashMap<String, String>();
        for (StatsReport.Value value : report.values) {
            reportMap.put(value.name, value.value);
        }
        return reportMap;
    }
    public void updateEncoderStatistics(final StatsReport[] reports) {

        final StringBuilder encoderStat = new StringBuilder(128);
        final StringBuilder bweStat = new StringBuilder();
        final StringBuilder connectionStat = new StringBuilder();
        final StringBuilder videoSendStat = new StringBuilder();
        final StringBuilder videoRecvStat = new StringBuilder();
        String fps = null;
        String targetBitrate = null;
        String actualBitrate = null;

        for (StatsReport report : reports) {
            if (report.type.equals("ssrc") && report.id.contains("ssrc")
                    && report.id.contains("send")) {
                // Send video statistics.
                Map<String, String> reportMap = getReportMap(report);
                String trackId = reportMap.get("googTrackId");
                if (trackId != null && trackId.contains(KPeerConnectionClient.VIDEO_TRACK_ID)) {
                    fps = reportMap.get("googFrameRateSent");
                    videoSendStat.append(report.id).append("\n");
                    for (StatsReport.Value value : report.values) {
                        String name = value.name.replace("goog", "");
                        videoSendStat.append(name).append("=").append(value.value).append("\n");
                    }
                }
            } else if (report.type.equals("ssrc") && report.id.contains("ssrc")
                    && report.id.contains("recv")) {
                // Receive video statistics.
                Map<String, String> reportMap = getReportMap(report);
                // Check if this stat is for video track.
                String frameWidth = reportMap.get("googFrameWidthReceived");
                if (frameWidth != null) {
                    videoRecvStat.append(report.id).append("\n");
                    for (StatsReport.Value value : report.values) {
                        String name = value.name.replace("goog", "");
                        videoRecvStat.append(name).append("=").append(value.value).append("\n");
                    }
                }
            } else if (report.id.equals("bweforvideo")) {
                // BWE statistics.
                Map<String, String> reportMap = getReportMap(report);
                targetBitrate = reportMap.get("googTargetEncBitrate");
                actualBitrate = reportMap.get("googActualEncBitrate");

                bweStat.append(report.id).append("\n");
                for (StatsReport.Value value : report.values) {
                    String name = value.name.replace("goog", "").replace("Available", "");
                    bweStat.append(name).append("=").append(value.value).append("\n");
                }
            } else if (report.type.equals("googCandidatePair")) {
                // Connection statistics.
                Map<String, String> reportMap = getReportMap(report);
                String activeConnection = reportMap.get("googActiveConnection");
                if (activeConnection != null && activeConnection.equals("true")) {
                    connectionStat.append(report.id).append("\n");
                    for (StatsReport.Value value : report.values) {
                        String name = value.name.replace("goog", "");
                        connectionStat.append(name).append("=").append(value.value).append("\n");
                    }
                }
            }
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hudStatBwe.setText(bweStat.toString());
                hudStatConnection.setText(connectionStat.toString());
                hudStatVideoSend.setText(videoSendStat.toString());
                hudStatVideoRecv.setText(videoRecvStat.toString());
            }
        });


        if (SharePrefUtil.getInstance(getActivity().getApplicationContext(), R.xml.setting_pref).getVedioAble()) {
            if (fps != null) {
                encoderStat.append("Fps:  ").append(fps).append("\n");
            }
            if (targetBitrate != null) {
                encoderStat.append("Target BR: ").append(targetBitrate).append("\n");
            }
            if (actualBitrate != null) {
                encoderStat.append("Actual BR: ").append(actualBitrate).append("\n");
            }
        }

        if (cpuMonitor.sampleCpuUtilization()) {
            encoderStat.append("CPU%: ")
                    .append(cpuMonitor.getCpuCurrent()).append("/")
                    .append(cpuMonitor.getCpuAvg3()).append("/")
                    .append(cpuMonitor.getCpuAvgAll());
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                encoderStatCall.setText(encoderStat.toString());

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
