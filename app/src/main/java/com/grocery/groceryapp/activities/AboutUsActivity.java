package com.grocery.groceryapp.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.grocery.groceryapp.R;
import com.grocery.groceryapp.databinding.ActivityAboutUsBinding;

public class AboutUsActivity extends AppCompatActivity {

    ActivityAboutUsBinding binding;
    private final String aboutUsImage = "https://media.istockphoto.com/id/1400739452/vector/about-us-web-header-design-icon-interconnected-symbol-of-company-profile-corporate.jpg?s=612x612&w=0&k=20&c=-zgp-xnEqh8zBEjNajlPZmDF5PXuqlXVUu7RjBf_UGU=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initAboutUsImage();
        initAboutUsText();


        setSupportActionBar(binding.toolbar);

        // set te back Icon on Tool Bar
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow_icon));

        // set the click Listener to back Icon on Tool Bar
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.INSTANCE.animateSlideDown(AboutUsActivity.this);
            }
        });


    }

    private void initAboutUsText() {

        binding.txtAboutUs.setText("In the bustling symphony of daily life, a single note can often feel jarringly discordant. The grocery run. The dreaded trek through crowded aisles, the mental gymnastics of list-making, the inevitable forgotten essential. We, the founders of [Grocery App], understand this dissonance all too well. It was from this frustration, this yearning for a smoother melody, that our app was born.\n" +
                "\n" +
                "We envisioned a world where grocery shopping wasn't a chore, but an empowering act of self-care. A world where fresh, high-quality ingredients awaited your selection, not your physical presence in a crowded store. We dreamt of a digital marketplace brimming with local bounty, delivered with care directly to your doorstep.\n" +
                "\n" +
                "Fueled by this passion, we assembled a team of dreamers and doers. Individuals with a deep love for good food, a keen eye for technology, and an unwavering commitment to customer satisfaction. Together, we embarked on a grocery odyssey, meticulously crafting an app that would become your one-stop shop for a culinary adventure.\n" +
                "\n" +
                "Here at [Grocery App], we believe in the transformative power of food. We believe that nourishing your body shouldn't be a logistical hurdle.  With our app, you can curate a symphony of flavors from the comfort of your couch. Explore a vibrant marketplace overflowing with seasonal delights. Locally sourced fruits and vegetables bursting with freshness, pantry staples that form the foundation of culinary creativity, and specialty items to ignite your inner gourmand.\n" +
                "\n" +
                "But our commitment extends far beyond convenience. We prioritize quality like a maestro conducts an orchestra, meticulously partnering with trusted farmers and suppliers who share our dedication to freshness and ethical practices. We understand that affordability is the rhythm that keeps the music playing.  That's why you'll find competitive prices alongside exciting deals and special offers, ensuring that a delicious and healthy lifestyle is accessible to all.\n" +
                "\n" +
                "Yet, [Grocery App] aspires to be more than just a digital marketplace. We envision a community built on the bedrock of exceptional customer service.  Your feedback is the chorus that guides our ongoing development.  We actively listen to your needs, suggestions, and even recipe recommendations. After all, a little inspiration in the kitchen never hurt anyone!\n" +
                "\n" +
                "So, join us on this culinary odyssey. Let [Grocery App] be your partner in navigating the exciting world of food.  Together, let's transform the grocery run from a discordant note into a harmonious symphony of convenience, quality, and affordability.  Let's reimagine grocery shopping, one delicious bite at a time.");

    }

    private void initAboutUsImage() {
        Glide.with(this)
                .load(aboutUsImage)
                .placeholder(R.drawable.loading_image)
                .into(binding.imgAboutUs);

    }


}