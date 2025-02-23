package zs.bottom.zsbottombar;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import zs.bottom.zsbottombar.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomBar.setOnItemSelectedListener(
                new ZsBottomBar.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int position) {
                        Toast.makeText(getApplicationContext(),"postion "+position,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
